// Global state
let currentUser = null;
let accounts = [];
let activeAccounts = [];
let closedAccounts = [];
let balances = [];
let transactions = [];

// API Configuration
const API_BASE_URL = 'http://localhost:8080/api';

// API Helper Functions
async function apiCall(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const finalOptions = { ...defaultOptions, ...options };

    try {
        const response = await fetch(url, finalOptions);

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }

        // Handle 204 No Content responses
        if (response.status === 204) {
            return null;
        }

        return await response.json();
    } catch (error) {
        console.error('API call failed:', error);
        throw error;
    }
}

// Utility functions
function generateId() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0;
        const v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

function generateAccountNumber() {
    const timestamp = Date.now().toString().slice(-6);
    const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
    return `ACC-${timestamp}-${random}`;
}

function formatCurrency(amount, currency = 'USD') {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: currency
    }).format(amount);
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Authentication functions
async function login(email, password) {
    try {
        const authData = await apiCall('/auth/login', {
            method: 'POST',
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        // Get customer data
        const customerData = await apiCall(`/customers/${authData.customerId}`);

        currentUser = {
            id: customerData.id,
            fullName: customerData.fullName,
            email: customerData.email,
            dateOfBirth: customerData.dateOfBirth
        };

        // Update user name in header
        document.getElementById('user-name').textContent = `Welcome, ${currentUser.fullName}`;

        // Load user data
        await loadUserData();

        showMainApp();
        return true;
    } catch (error) {
        console.error('Login failed:', error);
        showModal('Login Failed', error.message || 'Invalid email or password. Please try again.');
        return false;
    }
}

async function signup(userData) {
    try {
        const customerData = await apiCall('/customers', {
            method: 'POST',
            body: JSON.stringify({
                fullName: userData.fullName,
                email: userData.email,
                dateOfBirth: userData.dateOfBirth,
                password: userData.password
            })
        });

        showModal('Account Created', 'Your account has been created successfully! Please log in.');
        showLogin();
        return true;
    } catch (error) {
        console.error('Signup failed:', error);
        showModal('Signup Failed', error.message || 'An error occurred during signup.');
        return false;
    }
}

function logout() {
    currentUser = null;
    accounts = [];
    activeAccounts = [];
    closedAccounts = [];
    balances = [];
    transactions = [];
    showLogin();
}

// Data loading functions
async function loadUserData() {
    if (!currentUser) return;

    try {
        // Load accounts
        accounts = await apiCall(`/accounts/customer/${currentUser.id}`);

        // Separate active and closed accounts
        activeAccounts = accounts.filter(account => account.status === 'ACTIVE');
        closedAccounts = accounts.filter(account => account.status === 'CLOSED');

        // Load balances for all accounts
        balances = [];
        for (const account of accounts) {
            const accountBalances = await apiCall(`/balances/account/${account.id}`);
            balances.push(...accountBalances);
        }

        // Load transactions for all accounts
        transactions = [];
        for (const account of accounts) {
            try {
                // Try to load transactions by debit account
                const debitTransactions = await apiCall(`/transactions/debitAccount/${account.id}`);
                transactions.push(...debitTransactions);
            } catch (error) {
                console.warn(`Failed to load debit transactions for account ${account.id}:`, error);
            }

            try {
                // Try to load transactions by credit account
                const creditTransactions = await apiCall(`/transactions/creditAccount/${account.id}`);
                transactions.push(...creditTransactions);
            } catch (error) {
                console.warn(`Failed to load credit transactions for account ${account.id}:`, error);
            }
        }

        // Remove duplicates (in case a transaction appears in both debit and credit)
        const uniqueTransactions = [];
        const seenIds = new Set();
        for (const transaction of transactions) {
            if (!seenIds.has(transaction.id)) {
                seenIds.add(transaction.id);
                uniqueTransactions.push(transaction);
            }
        }
        transactions = uniqueTransactions;

        // Update displays
        updateDashboard();
        updateAccountsSection();
        updateClosedAccountsSection();
        updateTransactionsSection();
        updateTransferForm();
    } catch (error) {
        console.error('Failed to load user data:', error);
        showModal('Error', 'Failed to load account data. Please try refreshing the page.');
    }
}

// UI functions
function showLogin() {
    document.getElementById('auth-container').classList.remove('hidden');
    document.getElementById('banking-app').classList.add('hidden');
    document.getElementById('login-form').classList.add('active');
    document.getElementById('signup-form').classList.remove('active');
}

function showSignup() {
    document.getElementById('login-form').classList.remove('active');
    document.getElementById('signup-form').classList.add('active');
}

function showMainApp() {
    document.getElementById('auth-container').classList.add('hidden');
    document.getElementById('banking-app').classList.remove('hidden');
    showSection('dashboard');
}

function showSection(sectionName) {
    // Hide all sections
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.remove('active');
    });

    // Remove active class from all nav items
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
    });

    // Show selected section
    const targetSection = document.getElementById(`${sectionName}-section`);
    if (targetSection) {
        targetSection.classList.add('active');
    }

    // Add active class to selected nav item
    const targetNavItem = document.querySelector(`[data-section="${sectionName}"]`);
    if (targetNavItem) {
        targetNavItem.classList.add('active');
    }

    // Update section-specific data
    switch(sectionName) {
        case 'dashboard':
            updateDashboard();
            break;
        case 'accounts':
            updateAccountsSection();
            break;
        case 'closed-accounts':
            updateClosedAccountsSection();
            break;
        case 'transactions':
            updateTransactionsSection();
            break;
        case 'transfer':
            updateTransferForm();
            break;
    }
}

// Dashboard functions
function updateDashboard() {
    updateDashboardStats();
    updateRecentActivity();
}

function updateDashboardStats() {
    // Calculate total balance across all active accounts only
    const totalUSD = balances
        .filter(balance => {
            const account = accounts.find(acc => acc.id === balance.accountId);
            return account && account.status === 'ACTIVE' && balance.currency === 'USD';
        })
        .reduce((sum, balance) => sum + parseFloat(balance.amount), 0);

    document.getElementById('total-balance').textContent = formatCurrency(totalUSD);
    document.getElementById('account-count').textContent = activeAccounts.length;

    // Calculate this month's transactions
    const thisMonth = new Date();
    thisMonth.setDate(1);
    const thisMonthTransactions = transactions.filter(txn =>
        new Date(txn.createdAt) >= thisMonth
    );
    document.getElementById('transaction-count').textContent = thisMonthTransactions.length;
}

function updateRecentActivity() {
    const container = document.getElementById('recent-activity');
    container.innerHTML = '';

    const recentTransactions = transactions
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .slice(0, 3);

    if (recentTransactions.length === 0) {
        container.innerHTML = '<div class="text-center text-muted" style="padding: 40px;">No recent activity</div>';
        return;
    }

    recentTransactions.forEach(transaction => {
        const item = createTransactionItem(transaction);
        container.appendChild(item);
    });
}

// Account functions
function updateAccountsSection() {
    const container = document.getElementById('accounts-grid');
    container.innerHTML = '';

    if (activeAccounts.length === 0) {
        container.innerHTML = '<div class="text-center text-muted" style="padding: 40px; grid-column: 1 / -1;">No active accounts found. Create your first account to get started!</div>';
        return;
    }

    activeAccounts.forEach(account => {
        const accountCard = createAccountCard(account, false);
        container.appendChild(accountCard);
    });
}

function updateClosedAccountsSection() {
    const container = document.getElementById('closed-accounts-grid');
    container.innerHTML = '';

    if (closedAccounts.length === 0) {
        container.innerHTML = '<div class="text-center text-muted" style="padding: 40px; grid-column: 1 / -1;">No closed accounts found.</div>';
        return;
    }

    closedAccounts.forEach(account => {
        const accountCard = createAccountCard(account, true);
        container.appendChild(accountCard);
    });
}

function createAccountCard(account, isClosed = false) {
    const accountBalances = balances.filter(bal => bal.accountId === account.id);

    const card = document.createElement('div');
    card.className = `account-card ${isClosed ? 'closed-account' : ''}`;

    const typeClass = account.type.toLowerCase();
    const typeLabel = account.type.charAt(0) + account.type.slice(1).toLowerCase();

    card.innerHTML = `
        <div class="account-header">
            <h3>${typeLabel} Account ${isClosed ? '(CLOSED)' : ''}</h3>
            <span class="account-type-badge ${typeClass} ${isClosed ? 'closed' : ''}">${account.type}</span>
        </div>
        <div class="account-number">${account.accountNumber}</div>
        <div class="account-status">Status: <span class="status-${account.status.toLowerCase()}">${account.status}</span></div>
        ${!isClosed ? `
        <div class="account-balances">
            ${accountBalances.length > 0 ? accountBalances.map(balance => `
                <div class="balance-item">
                    <span class="currency-code">${balance.currency}</span>
                    <span class="balance-value">${formatCurrency(balance.amount, balance.currency)}</span>
                </div>
            `).join('') : '<div class="balance-item"><span class="currency-code">USD</span><span class="balance-value">$0.00</span></div>'}
        </div>
        ` : ''}
        <div class="account-actions">
            ${!isClosed ? `
                <button class="btn btn-danger btn-small close-account-btn" data-account-id="${account.id}">Close Account</button>
            ` : `
                <button class="btn btn-success btn-small restore-account-btn" data-account-id="${account.id}">Restore Account</button>
            `}
        </div>
    `;

    // Add event listeners
    if (!isClosed) {
        const closeBtn = card.querySelector('.close-account-btn');
        closeBtn.addEventListener('click', () => confirmCloseAccount(account));
    } else {
        const restoreBtn = card.querySelector('.restore-account-btn');
        restoreBtn.addEventListener('click', () => confirmRestoreAccount(account));
    }

    return card;
}

// Account management functions
function confirmCloseAccount(account) {
    const accountBalances = balances.filter(bal => bal.accountId === account.id);
    const totalBalance = accountBalances.reduce((sum, balance) => sum + parseFloat(balance.amount), 0);

    let confirmationMessage = `
        <p>Are you sure you want to close this account?</p>
        <p><strong>Account:</strong> ${account.accountNumber} (${account.type})</p>
    `;

    if (totalBalance > 0) {
        confirmationMessage += `
            <p><strong>Warning:</strong> This account has a balance of ${formatCurrency(totalBalance)}.
            Please transfer all funds before closing the account.</p>
            <p class="text-danger">Account closure will be blocked if there are remaining funds.</p>
        `;
    } else {
        confirmationMessage += `
            <p class="text-muted">This account has no remaining balance and can be safely closed.</p>
        `;
    }

    confirmationMessage += `
        <div class="modal-actions" style="margin-top: 20px;">
            <button onclick="hideModal()" class="btn btn-secondary">Cancel</button>
            <button onclick="closeAccount('${account.id}')" class="btn btn-danger">Close Account</button>
        </div>
    `;

    showModal('Close Account', confirmationMessage);
}

function confirmRestoreAccount(account) {
    let confirmationMessage = `
        <p>Are you sure you want to restore this account?</p>
        <p><strong>Account:</strong> ${account.accountNumber} (${account.type})</p>
        <p class="text-muted">This will reactivate the account and make it available for transactions.</p>
        <div class="modal-actions" style="margin-top: 20px;">
            <button onclick="hideModal()" class="btn btn-secondary">Cancel</button>
            <button onclick="restoreAccount('${account.id}')" class="btn btn-success">Restore Account</button>
        </div>
    `;

    showModal('Restore Account', confirmationMessage);
}

async function closeAccount(accountId) {
    try {
        // Check if account has any remaining balance
        const accountBalances = balances.filter(bal => bal.accountId === accountId);
        const totalBalance = accountBalances.reduce((sum, balance) => sum + parseFloat(balance.amount), 0);

        if (totalBalance > 0) {
            showModal('Cannot Close Account',
                `This account still has a balance of ${formatCurrency(totalBalance)}.
                Please transfer all funds to another account before closure.`);
            return;
        }

        // Close the account via API (soft delete)
        await apiCall(`/accounts/${accountId}`, {
            method: 'DELETE'
        });

        // Reload user data to reflect changes
        await loadUserData();

        hideModal();
        showModal('Account Closed', 'The account has been successfully closed.');

    } catch (error) {
        console.error('Account closure failed:', error);
        hideModal();
        showModal('Closure Failed', error.message || 'An error occurred while closing the account.');
    }
}

async function restoreAccount(accountId) {
    try {
        // Restore the account via API
        await apiCall(`/accounts/${accountId}/restore`, {
            method: 'PUT'
        });

        // Reload user data to reflect changes
        await loadUserData();

        hideModal();
        showModal('Account Restored', 'The account has been successfully restored and is now active.');

    } catch (error) {
        console.error('Account restoration failed:', error);
        hideModal();
        showModal('Restoration Failed', error.message || 'An error occurred while restoring the account.');
    }
}

async function createAccount(accountData) {
    try {
        // Create account
        const newAccount = await apiCall('/accounts', {
            method: 'POST',
            body: JSON.stringify({
                accountNumber: generateAccountNumber(),
                type: accountData.accountType,
                ownerId: currentUser.id
            })
        });

        // Create initial balance if deposit amount is provided
        if (accountData.initialDeposit && parseFloat(accountData.initialDeposit) > 0) {
            const newBalance = await apiCall('/balances', {
                method: 'POST',
                body: JSON.stringify({
                    accountId: newAccount.id,
                    amount: parseFloat(accountData.initialDeposit),
                    currency: accountData.depositCurrency
                })
            });

            // Create a deposit transaction
            const depositTransaction = await apiCall('/transactions', {
                method: 'POST',
                body: JSON.stringify({
                    referenceNumber: `TXN-${Date.now()}`,
                    debitAccountId: null,
                    creditAccountId: newAccount.id,
                    amount: parseFloat(accountData.initialDeposit),
                    currency: accountData.depositCurrency,
                    type: 'DEPOSIT',
                    status: 'COMPLETED'
                })
            });
        }

        // Reload user data
        await loadUserData();

        return newAccount;
    } catch (error) {
        console.error('Account creation failed:', error);
        throw error;
    }
}

function showAccountCreationModal() {
    document.getElementById('account-modal-overlay').classList.remove('hidden');
}

function hideAccountCreationModal() {
    document.getElementById('account-modal-overlay').classList.add('hidden');
    document.getElementById('create-account-form').reset();
    updateAccountTypeInfo('');
}

function updateAccountTypeInfo(accountType) {
    const infoDiv = document.getElementById('account-type-info');

    const accountTypeDescriptions = {
        'CHECKING': {
            title: 'Checking Account',
            description: 'Perfect for everyday transactions, bill payments, and direct deposits. No minimum balance required.',
            features: ['Unlimited transactions', 'Debit card included', 'Online banking', 'Mobile deposits']
        },
        'SAVINGS': {
            title: 'Savings Account',
            description: 'Earn interest on your deposits while keeping your money safe and accessible.',
            features: ['Competitive interest rates', 'FDIC insured', 'Online transfers', 'Automatic savings plans']
        },
        'CREDIT': {
            title: 'Credit Account',
            description: 'Build credit history with responsible spending and flexible payment options.',
            features: ['Credit building', 'Rewards program', 'Fraud protection', 'Emergency access to funds']
        }
    };

    if (accountType && accountTypeDescriptions[accountType]) {
        const info = accountTypeDescriptions[accountType];
        infoDiv.innerHTML = `
            <div class="account-info-card">
                <h4>${info.title}</h4>
                <p>${info.description}</p>
                <ul>
                    ${info.features.map(feature => `<li>${feature}</li>`).join('')}
                </ul>
            </div>
        `;
    } else {
        infoDiv.innerHTML = '<p class="text-muted">Select an account type to see details</p>';
    }
}

// Transaction functions
function updateTransactionsSection() {
    updateTransactionFilters();
    displayTransactions();
}

function updateTransactionFilters() {
    const accountFilter = document.getElementById('account-filter');
    accountFilter.innerHTML = '<option value="">All Accounts</option>';

    // Include both active and closed accounts in filter
    accounts.forEach(account => {
        const option = document.createElement('option');
        option.value = account.id;
        option.textContent = `${account.accountNumber} (${account.type}) ${account.status === 'CLOSED' ? '- CLOSED' : ''}`;
        accountFilter.appendChild(option);
    });
}

function displayTransactions(filteredTransactions = null) {
    const transactionsToShow = filteredTransactions || transactions;
    const container = document.getElementById('transactions-table');

    if (transactionsToShow.length === 0) {
        container.innerHTML = '<div class="text-center text-muted" style="padding: 40px;">No transactions found</div>';
        return;
    }

    const transactionList = document.createElement('div');
    transactionList.className = 'transaction-list';

    transactionsToShow
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .forEach(transaction => {
            const item = createTransactionItem(transaction);
            transactionList.appendChild(item);
        });

    container.innerHTML = '';
    container.appendChild(transactionList);
}

function createTransactionItem(transaction) {
    const item = document.createElement('div');
    item.className = 'transaction-item';

    const isCredit = transaction.creditAccountId && accounts.find(acc => acc.id === transaction.creditAccountId);
    const isDebit = transaction.debitAccountId && accounts.find(acc => acc.id === transaction.debitAccountId);

    let amountClass = '';
    let amountPrefix = '';

    if (isCredit && !isDebit) {
        amountClass = 'positive';
        amountPrefix = '+';
    } else if (isDebit && !isCredit) {
        amountClass = 'negative';
        amountPrefix = '-';
    }

    const debitAccount = accounts.find(acc => acc.id === transaction.debitAccountId);
    const creditAccount = accounts.find(acc => acc.id === transaction.creditAccountId);

    let details = '';
    if (transaction.type === 'TRANSFER' && debitAccount && creditAccount) {
        details = `From ${debitAccount.accountNumber} to ${creditAccount.accountNumber}`;
    } else if (debitAccount) {
        details = `From ${debitAccount.accountNumber}`;
    } else if (creditAccount) {
        details = `To ${creditAccount.accountNumber}`;
    } else if (transaction.externalAccountNumber) {
        details = `To ${transaction.externalAccountNumber}`;
        if (transaction.recipientName) {
            details += ` (${transaction.recipientName})`;
        }
    }

    item.innerHTML = `
        <div class="transaction-info">
            <div class="transaction-type">${transaction.type}</div>
            <div class="transaction-details">
                ${details} • ${formatDate(transaction.createdAt)}
            </div>
        </div>
        <div style="display: flex; align-items: center;">
            <div class="transaction-amount ${amountClass}">
                ${amountPrefix}${formatCurrency(transaction.amount, transaction.currency)}
            </div>
            <span class="transaction-status ${transaction.status.toLowerCase()}">${transaction.status}</span>
        </div>
    `;

    return item;
}

function filterTransactions() {
    const accountFilter = document.getElementById('account-filter').value;
    const statusFilter = document.getElementById('status-filter').value;
    const typeFilter = document.getElementById('type-filter').value;

    let filtered = transactions;

    if (accountFilter) {
        filtered = filtered.filter(txn =>
            txn.debitAccountId === accountFilter || txn.creditAccountId === accountFilter
        );
    }

    if (statusFilter) {
        filtered = filtered.filter(txn => txn.status === statusFilter);
    }

    if (typeFilter) {
        filtered = filtered.filter(txn => txn.type === typeFilter);
    }

    displayTransactions(filtered);
}

// Transfer functions
function updateTransferForm() {
    const fromAccount = document.getElementById('from-account');
    const toAccount = document.getElementById('to-account');

    fromAccount.innerHTML = '<option value="">Select account</option>';
    toAccount.innerHTML = '<option value="">Select account</option>';

    // Only show active accounts for transfers
    activeAccounts.forEach(account => {
        const option1 = document.createElement('option');
        option1.value = account.id;
        option1.textContent = `${account.accountNumber} (${account.type})`;
        fromAccount.appendChild(option1);

        const option2 = document.createElement('option');
        option2.value = account.id;
        option2.textContent = `${account.accountNumber} (${account.type})`;
        toAccount.appendChild(option2);
    });
}

function toggleTransferType() {
    const transferType = document.getElementById('transfer-type').value;
    const internalRow = document.getElementById('internal-transfer-row');
    const externalRow = document.getElementById('external-transfer-row');
    const toAccountSelect = document.getElementById('to-account');
    const externalAccountInput = document.getElementById('external-account-number');
    const recipientNameInput = document.getElementById('recipient-name');

    if (transferType === 'external') {
        internalRow.classList.add('hidden');
        externalRow.classList.remove('hidden');
        toAccountSelect.removeAttribute('required');
        externalAccountInput.setAttribute('required', 'required');
        recipientNameInput.setAttribute('required', 'required');
    } else {
        internalRow.classList.remove('hidden');
        externalRow.classList.add('hidden');
        toAccountSelect.setAttribute('required', 'required');
        externalAccountInput.removeAttribute('required');
        recipientNameInput.removeAttribute('required');
    }
}

async function processTransfer(formData) {
    try {
        const transferType = formData.transferType;
        let creditAccountId = null;
        let externalAccountNumber = null;
        let recipientName = null;

        if (transferType === 'internal') {
            creditAccountId = formData.toAccount;
        } else {
            externalAccountNumber = formData.externalAccountNumber;
            recipientName = formData.recipientName;
        }

        // Create transaction
        const transactionData = {
            referenceNumber: `TXN-${Date.now()}`,
            debitAccountId: formData.fromAccount,
            creditAccountId: creditAccountId,
            amount: parseFloat(formData.amount),
            currency: formData.currency,
            type: 'TRANSFER',
            status: 'COMPLETED'
        };

        if (externalAccountNumber) {
            transactionData.externalAccountNumber = externalAccountNumber;
        }
        if (recipientName) {
            transactionData.recipientName = recipientName;
        }
        if (formData.description) {
            transactionData.description = formData.description;
        }

        const newTransaction = await apiCall('/transactions', {
            method: 'POST',
            body: JSON.stringify(transactionData)
        });

        // Update balances for internal transfers
        if (transferType === 'internal') {
            // Debit from source account
            const fromAccountBalances = balances.filter(b => b.accountId === formData.fromAccount && b.currency === formData.currency);
            if (fromAccountBalances.length > 0) {
                const fromBalance = fromAccountBalances[0];
                await apiCall(`/balances/${fromBalance.id}`, {
                    method: 'PUT',
                    body: JSON.stringify({
                        ...fromBalance,
                        amount: parseFloat(fromBalance.amount) - parseFloat(formData.amount)
                    })
                });
            }

            // Credit to destination account
            const toAccountBalances = balances.filter(b => b.accountId === formData.toAccount && b.currency === formData.currency);
            if (toAccountBalances.length > 0) {
                const toBalance = toAccountBalances[0];
                await apiCall(`/balances/${toBalance.id}`, {
                    method: 'PUT',
                    body: JSON.stringify({
                        ...toBalance,
                        amount: parseFloat(toBalance.amount) + parseFloat(formData.amount)
                    })
                });
            } else {
                // Create new balance if it doesn't exist
                await apiCall('/balances', {
                    method: 'POST',
                    body: JSON.stringify({
                        accountId: formData.toAccount,
                        amount: parseFloat(formData.amount),
                        currency: formData.currency
                    })
                });
            }
        } else {
            // For external transfers, only debit from source account
            const fromAccountBalances = balances.filter(b => b.accountId === formData.fromAccount && b.currency === formData.currency);
            if (fromAccountBalances.length > 0) {
                const fromBalance = fromAccountBalances[0];
                await apiCall(`/balances/${fromBalance.id}`, {
                    method: 'PUT',
                    body: JSON.stringify({
                        ...fromBalance,
                        amount: parseFloat(fromBalance.amount) - parseFloat(formData.amount)
                    })
                });
            }
        }

        // Reload user data to reflect changes
        await loadUserData();

        let transferMessage = '';
        if (transferType === 'internal') {
            const toAccount = accounts.find(acc => acc.id === creditAccountId);
            transferMessage = `
                <p>Your transfer of ${formatCurrency(newTransaction.amount, newTransaction.currency)} has been completed successfully!</p>
                <p><strong>To:</strong> ${toAccount.accountNumber} (${toAccount.type})</p>
                <p><strong>Reference:</strong> ${newTransaction.referenceNumber}</p>
                <p><strong>Status:</strong> ${newTransaction.status}</p>
            `;
        } else {
            transferMessage = `
                <p>Your external transfer of ${formatCurrency(newTransaction.amount, newTransaction.currency)} has been initiated.</p>
                <p><strong>To:</strong> ${formData.externalAccountNumber}</p>
                <p><strong>Recipient:</strong> ${formData.recipientName}</p>
                <p><strong>Reference:</strong> ${newTransaction.referenceNumber}</p>
                <p><strong>Status:</strong> ${newTransaction.status}</p>
                <p class="text-muted mt-2">External transfers may take 1-3 business days to complete.</p>
            `;
        }

        showModal('Transfer Successful', transferMessage);
    } catch (error) {
        console.error('Transfer failed:', error);
        showModal('Transfer Failed', error.message || 'An error occurred while processing the transfer.');
    }
}

// Modal functions
function showModal(title, content) {
    document.getElementById('modal-title').textContent = title;
    document.getElementById('modal-body').innerHTML = content;
    document.getElementById('modal-overlay').classList.remove('hidden');
}

function hideModal() {
    document.getElementById('modal-overlay').classList.add('hidden');
}

// Event listeners
document.addEventListener('DOMContentLoaded', function() {
    // Auth toggle
    document.getElementById('show-signup').addEventListener('click', function(e) {
        e.preventDefault();
        showSignup();
    });

    document.getElementById('show-login').addEventListener('click', function(e) {
        e.preventDefault();
        showLogin();
    });

    // Login form
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const formData = new FormData(e.target);
        const email = formData.get('email');
        const password = formData.get('password');

        login(email, password).then(success => {
            if (success) {
                e.target.reset();
            }
        });
    });

    // Signup form
    document.getElementById('signupForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const formData = new FormData(e.target);
        const password = formData.get('password');
        const confirmPassword = formData.get('confirmPassword');

        if (password !== confirmPassword) {
            showModal('Signup Failed', 'Passwords do not match.');
            return;
        }

        const userData = {
            fullName: formData.get('fullName'),
            email: formData.get('email'),
            dateOfBirth: formData.get('dateOfBirth'),
            password: password
        };

        signup(userData).then(success => {
            if (success) {
                e.target.reset();
            }
        });
    });

    // Navigation
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', function() {
            const section = this.getAttribute('data-section');
            showSection(section);
        });
    });

    // Logout
    document.getElementById('logout-btn').addEventListener('click', logout);

    // Transaction filters
    document.getElementById('account-filter').addEventListener('change', filterTransactions);
    document.getElementById('status-filter').addEventListener('change', filterTransactions);
    document.getElementById('type-filter').addEventListener('change', filterTransactions);

    // Modal close handlers
    document.querySelector('.modal-close').addEventListener('click', hideModal);
    document.getElementById('modal-overlay').addEventListener('click', function(e) {
        if (e.target === this) {
            hideModal();
        }
    });

    // Transfer type toggle
    document.getElementById('transfer-type').addEventListener('change', toggleTransferType);

    // Transfer form
    document.getElementById('transfer-form').addEventListener('submit', function(e) {
        e.preventDefault();
        const formData = new FormData(e.target);
        const transferType = formData.get('transferType');

        const transferData = {
            transferType: transferType,
            fromAccount: formData.get('fromAccount'),
            toAccount: formData.get('toAccount'),
            externalAccountNumber: formData.get('externalAccountNumber'),
            recipientName: formData.get('recipientName'),
            amount: formData.get('amount'),
            currency: formData.get('currency'),
            description: formData.get('description')
        };

        // Validation
        if (transferType === 'internal' && transferData.fromAccount === transferData.toAccount) {
            showModal('Transfer Error', 'Cannot transfer to the same account.');
            return;
        }

        if (transferType === 'external' && (!transferData.externalAccountNumber || !transferData.recipientName)) {
            showModal('Transfer Error', 'Please provide external account number and recipient name.');
            return;
        }

        processTransfer(transferData).then(() => {
            e.target.reset();
            toggleTransferType(); // Reset form visibility
        });
    });

    // Account creation modal
    document.getElementById('create-account-btn').addEventListener('click', showAccountCreationModal);
    document.getElementById('account-modal-close').addEventListener('click', hideAccountCreationModal);
    document.getElementById('cancel-account-creation').addEventListener('click', hideAccountCreationModal);

    // Account type selection
    document.getElementById('account-type').addEventListener('change', function(e) {
        updateAccountTypeInfo(e.target.value);
    });

    // Account creation form
    document.getElementById('create-account-form').addEventListener('submit', function(e) {
        e.preventDefault();
        const formData = new FormData(e.target);

        const accountData = {
            accountType: formData.get('accountType'),
            initialDeposit: formData.get('initialDeposit'),
            depositCurrency: formData.get('depositCurrency')
        };

        createAccount(accountData).then(newAccount => {
            hideAccountCreationModal();

            showModal('Account Created Successfully', `
                <p>Your new ${accountData.accountType.toLowerCase()} account has been created!</p>
                <p><strong>Account Number:</strong> ${newAccount.accountNumber}</p>
                <p><strong>Account Type:</strong> ${newAccount.type}</p>
                ${accountData.initialDeposit && parseFloat(accountData.initialDeposit) > 0 ?
                    `<p><strong>Initial Deposit:</strong> ${formatCurrency(parseFloat(accountData.initialDeposit), accountData.depositCurrency)}</p>` :
                    ''
                }
                <p class="text-muted mt-2">You can now use this account for transfers and transactions.</p>
            `);
        }).catch(error => {
            showModal('Account Creation Failed', error.message || 'An error occurred while creating the account.');
        });
    });

    document.getElementById('account-modal-overlay').addEventListener('click', function(e) {
        if (e.target === this) {
            hideAccountCreationModal();
        }
    });

    // Initialize with login form
    showLogin();
});