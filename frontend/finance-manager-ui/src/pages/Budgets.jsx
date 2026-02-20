import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Budgets.css';

function Budgets() {
    const navigate = useNavigate();
    const [budgets, setBudgets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchBudgets();
    }, []);

    const fetchBudgets = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/budgets', {
                method: 'GET',
                credentials: 'include'
            });

            if (response.status === 401 || response.status === 403) {
                navigate('/login');
                return;
            }

            if (response.ok) {
                const data = await response.json();
                setBudgets(data);
            } else {
                setError('Failed to load budgets');
            }
        } catch (err) {
            setError('Failed to connect to server');
            console.error('Error:', err);
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

    const getMonthName = (month) => {
        const months = [
            'January', 'February', 'March', 'April', 'May', 'June',
            'July', 'August', 'September', 'October', 'November', 'December'
        ];
        return months[month - 1];
    };

    const getProgressColor = (percentage) => {
        if (percentage >= 100) return '#F44336';  // Red - over budget
        if (percentage >= 80) return '#FF9800';   // Orange - warning
        if (percentage >= 50) return '#FFC107';   // Yellow - caution
        return '#4CAF50';                          // Green - safe
    };

    const getStatusText = (percentage, alertTriggered) => {
        if (percentage >= 100) return '⚠️ Over Budget';
        if (alertTriggered) return '🔔 Alert Triggered';
        if (percentage >= 80) return '⚡ Nearly There';
        if (percentage >= 50) return '👍 On Track';
        return '✅ Good';
    };

    return (
        <div className="budgets-page">
            <div className="budgets-header">
                <h1>💼 My Budgets</h1>
                <button onClick={() => navigate('/set-budget')} className="add-btn">
                    ➕ Set Budget
                </button>
            </div>

            {loading && <div className="loading">Loading budgets...</div>}
            {error && <div className="error-message">{error}</div>}

            {!loading && budgets.length === 0 && (
                <div className="empty-state">
                    <p>📊 No budgets set yet</p>
                    <button onClick={() => navigate('/set-budget')} className="add-first-btn">
                        Set Your First Budget
                    </button>
                </div>
            )}

            <div className="budgets-grid">
                {budgets.map(budget => (
                    <div key={budget.id} className="budget-card">
                        <div className="budget-header">
                            <h3>{budget.categoryName}</h3>
                            <span className="budget-period">
                                {getMonthName(budget.month)} {budget.year}
                            </span>
                        </div>

                        <div className="budget-amounts">
                            <div className="amount-item">
                                <span className="amount-label">Spent</span>
                                <span className="amount-value spent">
                                    {formatCurrency(budget.spentAmount)}
                                </span>
                            </div>
                            <div className="amount-divider">/</div>
                            <div className="amount-item">
                                <span className="amount-label">Budget</span>
                                <span className="amount-value budget">
                                    {formatCurrency(budget.amount)}
                                </span>
                            </div>
                        </div>

                        <div className="budget-progress">
                            <div className="progress-bar">
                                <div
                                    className="progress-fill"
                                    style={{
                                        width: `${Math.min(budget.percentage, 100)}%`,
                                        backgroundColor: getProgressColor(budget.percentage)
                                    }}
                                ></div>
                            </div>
                            <span className="progress-text">{budget.percentage}%</span>
                        </div>

                        <div className="budget-footer">
                            <span className="budget-status">
                                {getStatusText(budget.percentage, budget.alertTriggered)}
                            </span>
                            <span className="budget-threshold">
                                Alert at {budget.alertThreshold}%
                            </span>
                        </div>

                        {budget.percentage >= budget.alertThreshold && (
                            <div className="budget-alert">
                                {budget.percentage >= 100
                                    ? `You've exceeded your budget by ${formatCurrency(budget.spentAmount - budget.amount)}`
                                    : `You've reached ${budget.percentage}% of your budget`
                                }
                            </div>
                        )}
                    </div>
                ))}
            </div>

            <div className="back-button">
                <button onClick={() => navigate('/dashboard')}>← Back to Dashboard</button>
            </div>
        </div>
    );
}

export default Budgets;