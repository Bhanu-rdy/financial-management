import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Categories.css';

function Categories() {
    const navigate = useNavigate();
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [filter, setFilter] = useState('ALL'); // ALL, INCOME, EXPENSE
    const [stats, setStats] = useState({
        totalCategories: 0,
        incomeCategories: 0,
        expenseCategories: 0
    });

    useEffect(() => {
        fetchCategories();
    }, []);

    useEffect(() => {
        calculateStats();
    }, [categories]);

    const fetchCategories = async () => {
        try {
            const response = await fetch('https://financial-management-e8o9.onrender.com/api/categories', {
                method: 'GET',
                credentials: 'include'
            });

            if (response.status === 401 || response.status === 403) {
                navigate('/login');
                return;
            }

            if (response.ok) {
                const data = await response.json();
                setCategories(data);
            } else {
                setError('Failed to load categories');
            }
        } catch (err) {
            setError('Failed to connect to server');
            console.error('Error:', err);
        } finally {
            setLoading(false);
        }
    };

    const calculateStats = () => {
        setStats({
            totalCategories: categories.length,
            incomeCategories: categories.filter(c => c.type === 'INCOME').length,
            expenseCategories: categories.filter(c => c.type === 'EXPENSE').length
        });
    };

    const getCategoryColor = (name, type) => {
        if (type === 'INCOME') {
            const incomeColors = ['#4CAF50', '#66BB6A', '#81C784', '#A5D6A7', '#2E7D32'];
            return incomeColors[Math.abs(hashCode(name)) % incomeColors.length];
        } else {
            const expenseColors = ['#FF5722', '#F44336', '#FF9800', '#FF6F00', '#E91E63'];
            return expenseColors[Math.abs(hashCode(name)) % expenseColors.length];
        }
    };

    const hashCode = (str) => {
        let hash = 0;
        for (let i = 0; i < str.length; i++) {
            hash = str.charCodeAt(i) + ((hash << 5) - hash);
        }
        return hash;
    };

    const getCategoryIcon = (name) => {
        const lowerName = name.toLowerCase();
        if (lowerName.includes('salary') || lowerName.includes('income') || lowerName.includes('wage')) return '💰';
        if (lowerName.includes('freelance') || lowerName.includes('business')) return '💼';
        if (lowerName.includes('investment') || lowerName.includes('dividend')) return '📈';
        if (lowerName.includes('gift') || lowerName.includes('bonus')) return '🎁';
        if (lowerName.includes('grocery') || lowerName.includes('food') || lowerName.includes('restaurant')) return '🛒';
        if (lowerName.includes('rent') || lowerName.includes('house') || lowerName.includes('mortgage')) return '🏠';
        if (lowerName.includes('transport') || lowerName.includes('car') || lowerName.includes('fuel') || lowerName.includes('gas')) return '🚗';
        if (lowerName.includes('entertainment') || lowerName.includes('fun') || lowerName.includes('movie')) return '🎮';
        if (lowerName.includes('health') || lowerName.includes('medical') || lowerName.includes('doctor')) return '🏥';
        if (lowerName.includes('education') || lowerName.includes('school') || lowerName.includes('course')) return '📚';
        if (lowerName.includes('shopping') || lowerName.includes('clothes') || lowerName.includes('fashion')) return '🛍️';
        if (lowerName.includes('utility') || lowerName.includes('bill') || lowerName.includes('electricity')) return '💡';
        if (lowerName.includes('insurance')) return '🛡️';
        if (lowerName.includes('fitness') || lowerName.includes('gym') || lowerName.includes('sport')) return '💪';
        if (lowerName.includes('travel') || lowerName.includes('vacation') || lowerName.includes('trip')) return '✈️';
        if (lowerName.includes('pet')) return '🐕';
        if (lowerName.includes('phone') || lowerName.includes('internet') || lowerName.includes('mobile')) return '📱';
        return '📌';
    };

    const filteredCategories = categories.filter(cat => {
        if (filter === 'ALL') return true;
        return cat.type === filter;
    });

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    };

    return (
        <div className="categories-page">
            <div className="categories-header">
                <h1>📂 My Categories</h1>
                <button onClick={() => navigate('/add-category')} className="add-btn">
                    ➕ Add Category
                </button>
            </div>

            {/* Stats Summary */}
            <div className="categories-stats">
                <div className="stat-box">
                    <div className="stat-icon">📊</div>
                    <div className="stat-content">
                        <p className="stat-value">{stats.totalCategories}</p>
                        <p className="stat-label">Total Categories</p>
                    </div>
                </div>

                <div className="stat-box income-box">
                    <div className="stat-icon">💰</div>
                    <div className="stat-content">
                        <p className="stat-value">{stats.incomeCategories}</p>
                        <p className="stat-label">Income Categories</p>
                    </div>
                </div>

                <div className="stat-box expense-box">
                    <div className="stat-icon">💸</div>
                    <div className="stat-content">
                        <p className="stat-value">{stats.expenseCategories}</p>
                        <p className="stat-label">Expense Categories</p>
                    </div>
                </div>
            </div>

            {/* Filter Tabs */}
            <div className="filter-tabs">
                <button
                    className={filter === 'ALL' ? 'active' : ''}
                    onClick={() => setFilter('ALL')}
                >
                    All ({stats.totalCategories})
                </button>
                <button
                    className={filter === 'INCOME' ? 'active' : ''}
                    onClick={() => setFilter('INCOME')}
                >
                    💰 Income ({stats.incomeCategories})
                </button>
                <button
                    className={filter === 'EXPENSE' ? 'active' : ''}
                    onClick={() => setFilter('EXPENSE')}
                >
                    💸 Expense ({stats.expenseCategories})
                </button>
            </div>

            {loading && <div className="loading">Loading categories...</div>}
            {error && <div className="error-message">{error}</div>}

            {!loading && filteredCategories.length === 0 && (
                <div className="empty-state">
                    <div className="empty-icon">📂</div>
                    <p className="empty-title">No categories found</p>
                    <p className="empty-subtitle">
                        {filter === 'ALL'
                            ? "Create your first category to start organizing your finances"
                            : `No ${filter.toLowerCase()} categories yet`
                        }
                    </p>
                    <button onClick={() => navigate('/add-category')} className="add-first-btn">
                        ➕ Create Category
                    </button>
                </div>
            )}

            <div className="categories-grid">
                {filteredCategories.map(category => (
                    <div
                        key={category.id}
                        className="category-card"
                        style={{ borderLeft: `5px solid ${getCategoryColor(category.name, category.type)}` }}
                    >
                        <div
                            className="category-icon"
                            style={{ background: getCategoryColor(category.name, category.type) }}
                        >
                            {getCategoryIcon(category.name)}
                        </div>

                        <div className="category-info">
                            <h3>{category.name}</h3>
                            <span className={`category-type ${category.type.toLowerCase()}`}>
                                {category.type === 'INCOME' ? '💰 Income' : '💸 Expense'}
                            </span>
                            <p className="category-date">
                                Created: {formatDate(category.createdAt)}
                            </p>
                        </div>
                    </div>
                ))}
            </div>

            <div className="action-buttons">
                <button onClick={() => navigate('/dashboard')} className="back-btn">
                    ← Back to Dashboard
                </button>
                <button onClick={() => navigate('/transactions')} className="view-transactions-btn">
                    View Transactions →
                </button>
            </div>
        </div>
    );
}

export default Categories;