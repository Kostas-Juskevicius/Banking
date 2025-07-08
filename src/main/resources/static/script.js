// Global state
let currentUser = null;
let accounts = [];
let balances = [];
let transactions = [];

// Mock data for demonstration
const mockData = {
    users: [
        {
            id: '550e8400-e29b-41d4-a716-446655440000',
            fullName: 'John Doe',
            email: 'john@example.com',
            password: 'password123',
            dateOfBirth: '1990-05-15'
        }
    ],
    accounts: [
        {
            id: '550e8400-e29b-41d4-a716-446655440001',
            accountNumber: 'ACC-001-2024',
            type: 'CHECKING',
            ownerId: '550e8400-e29b-41d4-a716-446655440000'
        },
        {
            id: '550e8400-e29b-41d4-a716-446655440002',
            accountNumber: 'ACC-002-2024',
            type: 'SAVINGS',
            ownerId: '550e8400-e29b-41d4-a716-446655440000'
        },
        {
            id: '550e8400-e29b-41d4-a716-446655440003',
            accountNumber: 'ACC-003-2024',
            type: 'CREDIT',
            ownerId: '550e8400-e29b-41d4-a716-446655440000'
        }
    ],
    balances: [
        {
            id: '550e8400-e29b-41d4-a716-446655440010',
            accountId: '550e8400-e29b-41d4-a716-446655440001',
            amount: 5420.75,
            currency: 'USD'
        },
        {
            id: '550e8400-e29b-41d4-a716-446655440011',
            accountId: '550e8400-e29b-41d4-a716-446655440001',
            amount: 1250.30,
            currency: 'EUR'
        },
        {
            id: '550e8400-e29b-41d4-a716-446655440012',
            accountId: '550e8400-e29b-41d4-a716-446655440002',
            amount: 12500.00,
            currency: 'USD'
        },
        {
            id: '550e8400-e29b-41d4-a716-446655440013',
            accountId: '550e8400-e29b-41d4-a716-446655440003',
            amount: -850.25,
            currency: 'USD'
        }
    ],
    transactions: [
        {
            id: '550e8400-e29b-41d4-a716-446655440020',
            referenceNumber: 'TXN-001-2024',
            debitAccountId: '550e8400-e29b-41d4-a716-446655440001',
            creditAccountId: null,
            amount: 150.00,
            currency: 'USD',
            type: 'WITHDRAWAL',
            status: 'COMPLETED',
            createdAt: '2024-01-15T10:30:00',
            postedAt: '2024-01-15T10:30:05'
        },
        {
            id: '550e8400-e29b-41d4-a716-446655440021',
            referenceNumber: 'TXN-002-2024',
            debitAccountId: null,
            creditAccountId: '550e8400-e29b-41d4-a716-446655440002',
            amount: 2500.00,
            currency: 'USD',
            type: 'DEPOSIT',
            status: 'COMPLETED',
            createdAt: '2024-01-14T14:20:00',
            postedAt: '2024-01-14T14:20:02'
        },
        {
            id: '550e8400-e29b-41d4-a716-446655440022',
            referenceNumber: 'TXN-003-2024',
            debitAccountId: '550e8400-e29b-41d4-a716-446655440001',
            creditAccountId: '550e8400-e29b-41d4-a716-446655440002',
            amount: 500.00,
            currency: 'USD',
            type: 'TRANSFER',
            status: 'PENDING',
            createdAt: '2024-01-16T09:15:00',
            postedAt: null
        }
    ]
};

// Utility functions
function generateId() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0;
        const v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
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
function showLogin() {
    document.getElementById('login-form').classList.add('active');
    document.getElementById('signup-form').classList.remove('active');
}

function showSignup() {
    document.getElementById('signup-form').classList.add('active');
    document.getElementById('login-form').classList.remove('active');
}

function login(email, password) {
    const user = mockData.users.find(u => u.email === email && u.password === password);
    if (user) {
        currentUser = user;
        loadUserData();
        showBankingApp();
        return true;
    }
    return false;
}

function signup(userData) {
    // Check if email already exists
    if (mockData.users.find(u => u.email === userData.email)) {
        return false;
    }
    
    const newUser = {
        id: generateId(),
        ...userData
    };
    
    mockData.users.push(newUser);
    currentUser = newUser;
    loadUserData();
    showBankingApp();
    return true;
}

function logout() {
    currentUser = null;
    accounts = [];
    balances = [];
    transactions = [];
    showAuthContainer();
}

function showAuthContainer() {
    document.getElementById('auth-container').classList.remove('hidden');
    document.getElementById('banking-app').classList.add('hidden');
}

function showBankingApp() {
    document.getElementById('auth-container').classList.add('hidden');
    document.getElementById('banking-app').classList.remove('hidden');
    document.getElementById('user-name').textContent = `Welcome, ${currentUser.fullName}`;
    showSection('dashboard');
}

// Data loading functions
function loadUserData() {
    if (!currentUser) return;
    
    // Load user's accounts
    accounts = mockData.accounts.filter(acc => acc.ownerId === currentUser.id);
    
    // Load balances for user's accounts
    const accountIds = accounts.map(acc => acc.id);
    balances = mockData.balances.filter(bal => accountIds.includes(bal.accountId));
    
    // Load transactions for user's accounts
    transactions = mockData.transactions.filter(txn => 
        accountIds.includes(txn.debitAccountId) || accountIds.includes(txn.creditAccountId)
    );
    
    updateDashboard();
    updateAccountsSection();
    updateTransactionsSection();
    updateTransferForm();
}

// Navigation functions
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
    document.getElementById(`${sectionName}-section`).classList.add('active');
    
    // Add active class to selected nav item
    document.querySelector(`[data-section="${sectionName}"]`).classList.add('active');
}

// Dashboard functions
function updateDashboard() {
    // Calculate total balance
    const totalBalance = balances.reduce((total, balance) => {
        // Convert all to USD for simplicity (in real app, use exchange rates)
        return total + balance.amount;
    }, 0);
    
    document.getElementById('total-balance').textContent = formatCurrency(totalBalance);
    document.getElementById('account-count').textContent = accounts.length;
    document.getElementById('transaction-count').textContent = transactions.length;
    
    // Update recent transactions
    updateRecentTransactions();
}

function updateRecentTransactions() {
    const recentTransactions = transactions
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .slice(0, 5);
    
    const container = document.getElementById('recent-transactions');
    container.innerHTML = '';
    
    if (recentTransactions.length === 0) {
        container.innerHTML = '<p class="text-muted">No recent transactions</p>';
        return;
    }
    
    recentTransactions.forEach(transaction => {
        const item = createTransactionItem(transaction);
        container.appendChild(item);
    });
}

// Accounts functions
function updateAccountsSection() {
    const container = document.getElementById('accounts-grid');
    container.innerHTML = '';
    
    accounts.forEach(account => {
        const accountCard = createAccountCard(account);
        container.appendChild(accountCard);
    });
}

function createAccountCard(account) {
    const accountBalances = balances.filter(bal => bal.accountId === account.id);
    
    const card = document.createElement('div');
    card.className = 'account-card';
    
    const typeClass = account.type.toLowerCase();
    const typeName = account.type.charAt(0) + account.type.slice(1).toLowerCase();
    
    card.innerHTML = `
        <div class="account-header">
            <div>
                <div class="account-type">${typeName} Account</div>
                <div class="account-number">${account.accountNumber}</div>
            </div>
            <span class="account-type-badge ${typeClass}">${account.type}</span>
        </div>
        <div class="account-balances">
            ${accountBalances.map(balance => `
                <div class="balance-item">
                    <span class="currency-code">${balance.currency}</span>
                    <span class="balance-value">${formatCurrency(balance.amount, balance.currency)}</span>
                </div>
            `).join('')}
        </div>
    `;
    
    return card;
}

// Transactions functions
function updateTransactionsSection() {
    updateTransactionFilters();
    displayTransactions();
}

function updateTransactionFilters() {
    const accountFilter = document.getElementById('account-filter');
    accountFilter.innerHTML = '<option value="">All Accounts</option>';
    
    accounts.forEach(account => {
        const option = document.createElement('option');
        option.value = account.id;
        option.textContent = `${account.accountNumber} (${account.type})`;
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
    
    accounts.forEach(account => {
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

function processTransfer(formData) {
    const newTransaction = {
        id: generateId(),
        referenceNumber: `TXN-${Date.now()}`,
        debitAccountId: formData.fromAccount,
        creditAccountId: formData.toAccount,
        amount: parseFloat(formData.amount),
        currency: formData.currency,
        type: 'TRANSFER',
        status: 'PENDING',
        createdAt: new Date().toISOString(),
        postedAt: null
    };
    
    mockData.transactions.push(newTransaction);
    transactions.push(newTransaction);
    
    // Update displays
    updateDashboard();
    updateTransactionsSection();
    
    showModal('Transfer Successful', `
        <p>Your transfer of ${formatCurrency(newTransaction.amount, newTransaction.currency)} has been initiated.</p>
        <p><strong>Reference:</strong> ${newTransaction.referenceNumber}</p>
        <p><strong>Status:</strong> Pending</p>
    `);
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
    // Auth form toggles
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
        
        if (login(email, password)) {
            e.target.reset();
        } else {
            showModal('Login Failed', 'Invalid email or password. Please try again.');
        }
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
        
        if (signup(userData)) {
            e.target.reset();
        } else {
            showModal('Signup Failed', 'An account with this email already exists.');
        }
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
    
    // Transfer form
    document.getElementById('transfer-form').addEventListener('submit', function(e) {
        e.preventDefault();
        const formData = new FormData(e.target);
        const transferData = {
            fromAccount: formData.get('fromAccount'),
            toAccount: formData.get('toAccount'),
            amount: formData.get('amount'),
            currency: formData.get('currency'),
            description: formData.get('description')
        };
        
        if (transferData.fromAccount === transferData.toAccount) {
            showModal('Transfer Error', 'Cannot transfer to the same account.');
            return;
        }
        
        processTransfer(transferData);
        e.target.reset();
    });
    
    // Modal close
    document.querySelector('.modal-close').addEventListener('click', hideModal);
    document.getElementById('modal-overlay').addEventListener('click', function(e) {
        if (e.target === this) {
            hideModal();
        }
    });
    
    // Create account button (placeholder)
    document.getElementById('create-account-btn').addEventListener('click', function() {
        showModal('Create Account', `
            <p>Account creation feature would be implemented here.</p>
            <p>This would typically include:</p>
            <ul>
                <li>Account type selection</li>
                <li>Initial deposit amount</li>
                <li>Terms and conditions</li>
                <li>Identity verification</li>
            </ul>
        `);
    });
    
    // Initialize with login form
    showLogin();
});

