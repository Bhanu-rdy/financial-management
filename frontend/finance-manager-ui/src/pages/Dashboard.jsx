import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';

function Dashboard() {
    const navigate = useNavigate();
    const [userData, setUserData] = useState({
        userName: '',
        email: '',
        firstName: '',
        lastName: ''
    });
    const [stats, setStats] = useState({
        totalIncome: 0,
        totalExpense: 0,
        netBalance: 0,
        totalGoals: 0
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const userId = localStorage.getItem('userId');
        const userName = localStorage.getItem('userName');
        const email = localStorage.getItem('email');
        const firstName = localStorage.getItem('firstName');
        const lastName = localStorage.getItem('lastName');

        if (!userId || !userName) {
            navigate('/login');
            return;
        }

        setUserData({
            userName: userName || 'User',
            email: email || '',
            firstName: firstName || '',
            lastName: lastName || ''
        });

        // Fetch dashboard stats
        fetchDashboardStats();
    }, [navigate]);

    const fetchDashboardStats = async () => {
        try {
            const response = await fetch('https://financial-management-e8o9.onrender.com/api/dashboard/stats', {
                method: 'GET',
                credentials: 'include'
            });

            if (response.ok) {
                const data = await response.json();
                setStats({
                    totalIncome: data.totalIncome || 0,
                    totalExpense: data.totalExpense || 0,
                    netBalance: data.netBalance || 0,
                    totalGoals: data.totalGoals || 0
                });
            }
        } catch (err) {
            console.error('Error fetching stats:', err);
        } finally {
            setLoading(false);
        }
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    };

    const handleLogout = async () => {
        const confirmLogout = window.confirm('Are you sure you want to logout?');

        if (confirmLogout) {
            try {
                await fetch('http://localhost:8080/api/auth/logout', {
                    method: 'POST',
                    credentials: 'include'
                });
            } catch (err) {
                console.error('Logout error:', err);
            }

            localStorage.removeItem('userId');
            localStorage.removeItem('userName');
            localStorage.removeItem('email');
            localStorage.removeItem('firstName');
            localStorage.removeItem('lastName');

            navigate('/login');
        }
    };

    return (
        <div className="dashboard-container">
            <div className="dashboard-header">
                <div className="header-left">
                    <h1>💰 Finance Manager</h1>
                </div>
                <div className="header-right">
                    <span className="user-name">Hi, {userData.firstName || userData.userName}!</span>
                    <button onClick={handleLogout} className="logout-btn">Logout</button>
                </div>
            </div>

            <div className="dashboard-content">
                {/* Welcome Message */}
                <div className="welcome-message">
                    <h2>Welcome back, {userData.firstName}! 👋</h2>
                    <p>Here's your financial overview</p>
                </div>

                {/* Stats Grid */}
                <div className="stats-grid">
                    <div className="stat-card income-card">
                        <div className="stat-icon">💵</div>
                        <div className="stat-info">
                            <p className="stat-label">Total Income</p>
                            <p className="stat-value">
                                {loading ? '...' : formatCurrency(stats.totalIncome)}
                            </p>
                        </div>
                    </div>

                    <div className="stat-card expense-card">
                        <div className="stat-icon">💸</div>
                        <div className="stat-info">
                            <p className="stat-label">Total Expenses</p>
                            <p className="stat-value">
                                {loading ? '...' : formatCurrency(stats.totalExpense)}
                            </p>
                        </div>
                    </div>

                    <div className={`stat-card balance-card ${stats.netBalance >= 0 ? 'positive' : 'negative'}`}>
                        <div className="stat-icon">
                            {stats.netBalance >= 0 ? '💰' : '⚠️'}
                        </div>
                        <div className="stat-info">
                            <p className="stat-label">Net Balance</p>
                            <p className="stat-value">
                                {loading ? '...' : formatCurrency(stats.netBalance)}
                            </p>
                        </div>
                    </div>

                    <div className="stat-card goals-card">
                        <div className="stat-icon">🎯</div>
                        <div className="stat-info">
                            <p className="stat-label">Savings Goals</p>
                            <p className="stat-value">
                                {loading ? '...' : stats.totalGoals}
                            </p>
                        </div>
                    </div>
                </div>

                {/* Quick Actions */}
                <div className="quick-actions">
                    <h3>Quick Actions</h3>
                    <div className="action-grid">
                        <button
                            className="action-btn"
                            onClick={() => navigate('/add-category')}
                        >
                            <span className="btn-icon">➕</span>
                            <span>Add Category</span>
                        </button>

                        <button
                            className="action-btn"
                            onClick={() => navigate('/categories')}
                        >
                            <span className="btn-icon">📊</span>
                            <span>View Categories</span>
                        </button>

                        <button
                            className="action-btn"
                            onClick={() => navigate('/add-transaction')}
                        >
                            <span className="btn-icon">💳</span>
                            <span>Add Transaction</span>
                        </button>

                        <button
                            className="action-btn"
                            onClick={() => navigate('/transactions')}
                        >
                            <span className="btn-icon">📋</span>
                            <span>View Transactions</span>
                        </button>

                        <button
                            className="action-btn"
                            onClick={() => navigate('/set-budget')}
                        >
                            <span className="btn-icon">💼</span>
                            <span>Set Budget</span>
                        </button>

                        <button
                            className="action-btn"
                            onClick={() => navigate('/budgets')}
                        >
                            <span className="btn-icon">📈</span>
                            <span>View Budgets</span>
                        </button>

                        <button
                            className="action-btn"
                            onClick={() => navigate('/add-savings-goal')}
                        >
                            <span className="btn-icon">🎯</span>
                            <span>Add Savings Goal</span>
                        </button>

                        <button
                            className="action-btn"
                            onClick={() => navigate('/savings-goals')}
                        >
                            <span className="btn-icon">🏆</span>
                            <span>View Goals</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Dashboard;