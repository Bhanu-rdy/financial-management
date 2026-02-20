import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Transactions.css';

function Transactions() {
    const navigate = useNavigate();
    const [transactions, setTransactions] = useState([]);
    const [filteredTransactions, setFilteredTransactions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [filter, setFilter] = useState('ALL'); // ALL, INCOME, EXPENSE
    const [stats, setStats] = useState({
        totalIncome: 0,
        totalExpense: 0,
        netBalance: 0
    });

    useEffect(() => {
        fetchTransactions();
    }, []);

    useEffect(() => {
        filterTransactions();
    }, [filter, transactions]);

    const fetchTransactions = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/transactions', {
                method: 'GET',
                credentials: 'include'
            });

            if (response.status === 401 || response.status === 403) {
                navigate('/login');
                return;
            }

            if (response.ok) {
                const data = await response.json();
                setTransactions(data);
                calculateStats(data);
            } else {
                setError('Failed to load transactions');
            }
        } catch (err) {
            setError('Failed to connect to server');
            console.error('Error:', err);
        } finally {
            setLoading(false);
        }
    };

    const calculateStats = (transactionList) => {
        const income = transactionList
            .filter(t => t.type === 'INCOME')
            .reduce((sum, t) => sum + parseFloat(t.amount), 0);

        const expense = transactionList
            .filter(t => t.type === 'EXPENSE')
            .reduce((sum, t) => sum + parseFloat(t.amount), 0);

        setStats({
            totalIncome: income,
            totalExpense: expense,
            netBalance: income - expense
        });
    };

    const filterTransactions = () => {
        if (filter === 'ALL') {
            setFilteredTransactions(transactions);
        } else {
            setFilteredTransactions(transactions.filter(t => t.type === filter));
        }
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    };

    const getCategoryIcon = (name) => {
        const lowerName = name.toLowerCase();
        if (lowerName.includes('salary') || lowerName.includes('income')) return '💰';
        if (lowerName.includes('grocery') || lowerName.includes('food')) return '🛒';
        if (lowerName.includes('rent') || lowerName.includes('house')) return '🏠';
        if (lowerName.includes('transport') || lowerName.includes('car')) return '🚗';
        if (lowerName.includes('entertainment') || lowerName.includes('fun')) return '🎮';
        if (lowerName.includes('health') || lowerName.includes('medical')) return '🏥';
        if (lowerName.includes('education') || lowerName.includes('school')) return '📚';
        if (lowerName.includes('shopping') || lowerName.includes('clothes')) return '🛍️';
        return '📌';
    };

    return (
        <div className="transactions-page">
            <div className="transactions-header">
                <h1>💳 My Transactions</h1>
                <button onClick={() => navigate('/add-transaction')} className="add-btn">
                    ➕ Add Transaction
                </button>
            </div>

            {/* Stats Summary */}
            <div className="stats-summary">
                <div className="stat-card income">
                    <div className="stat-icon">💰</div>
                    <div className="stat-info">
                        <p className="stat-label">Total Income</p>
                        <p className="stat-value">{formatCurrency(stats.totalIncome)}</p>
                    </div>
                </div>

                <div className="stat-card expense">
                    <div className="stat-icon">💸</div>
                    <div className="stat-info">
                        <p className="stat-label">Total Expense</p>
                        <p className="stat-value">{formatCurrency(stats.totalExpense)}</p>
                    </div>
                </div>

                <div className={`stat-card balance ${stats.netBalance >= 0 ? 'positive' : 'negative'}`}>
                    <div className="stat-icon">{stats.netBalance >= 0 ? '✅' : '⚠️'}</div>
                    <div className="stat-info">
                        <p className="stat-label">Net Balance</p>
                        <p className="stat-value">{formatCurrency(stats.netBalance)}</p>
                    </div>
                </div>
            </div>

            {/* Filter Tabs */}
            <div className="filter-tabs">
                <button
                    className={filter === 'ALL' ? 'active' : ''}
                    onClick={() => setFilter('ALL')}
                >
                    All ({transactions.length})
                </button>
                <button
                    className={filter === 'INCOME' ? 'active' : ''}
                    onClick={() => setFilter('INCOME')}
                >
                    💰 Income ({transactions.filter(t => t.type === 'INCOME').length})
                </button>
                <button
                    className={filter === 'EXPENSE' ? 'active' : ''}
                    onClick={() => setFilter('EXPENSE')}
                >
                    💸 Expense ({transactions.filter(t => t.type === 'EXPENSE').length})
                </button>
            </div>

            {loading && <div className="loading">Loading transactions...</div>}
            {error && <div className="error-message">{error}</div>}

            {!loading && filteredTransactions.length === 0 && (
                <div className="empty-state">
                    <p>📋 No transactions yet</p>
                    <button onClick={() => navigate('/add-transaction')} className="add-first-btn">
                        Add Your First Transaction
                    </button>
                </div>
            )}

            {/* Transactions List */}
            <div className="transactions-list">
                {filteredTransactions.map(transaction => (
                    <div
                        key={transaction.id}
                        className={`transaction-card ${transaction.type.toLowerCase()}`}
                    >
                        <div className="transaction-icon">
                            {getCategoryIcon(transaction.categoryName)}
                        </div>

                        <div className="transaction-details">
                            <div className="transaction-main">
                                <h3>{transaction.categoryName}</h3>
                                <p className={`transaction-amount ${transaction.type.toLowerCase()}`}>
                                    {transaction.type === 'INCOME' ? '+' : '-'}
                                    {formatCurrency(transaction.amount)}
                                </p>
                            </div>

                            {transaction.description && (
                                <p className="transaction-description">{transaction.description}</p>
                            )}

                            <div className="transaction-footer">
                                <span className="transaction-date">
                                    📅 {formatDate(transaction.transactionDate)}
                                </span>
                                <span className={`transaction-type ${transaction.type.toLowerCase()}`}>
                                    {transaction.type}
                                </span>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            <div className="back-button">
                <button onClick={() => navigate('/dashboard')}>← Back to Dashboard</button>
            </div>
        </div>
    );
}

export default Transactions;