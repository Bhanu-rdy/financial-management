import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './SavingsGoals.css';

function SavingsGoals() {
    const navigate = useNavigate();
    const [goals, setGoals] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchSavingsGoals();
    }, []);

    const fetchSavingsGoals = async () => {
        try {
            const response = await fetch('https://financial-management-e8o9.onrender.com/api/savings', {
                method: 'GET',
                credentials: 'include'
            });

            if (response.status === 401 || response.status === 403) {
                navigate('/login');
                return;
            }

            if (response.ok) {
                const data = await response.json();
                setGoals(data);
            } else {
                setError('Failed to load savings goals');
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

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    };

    const getProgressColor = (percentage) => {
        if (percentage >= 100) return '#4CAF50';  // Green - completed
        if (percentage >= 75) return '#8BC34A';   // Light green
        if (percentage >= 50) return '#FFC107';   // Yellow
        if (percentage >= 25) return '#FF9800';   // Orange
        return '#FF5722';                          // Red
    };

    const getStatusIcon = (completed, daysRemaining) => {
        if (completed) return '✅';
        if (daysRemaining < 0) return '⏰';
        if (daysRemaining <= 7) return '🔥';
        if (daysRemaining <= 30) return '⚡';
        return '🎯';
    };

    const getStatusText = (completed, daysRemaining) => {
        if (completed) return 'Completed!';
        if (daysRemaining < 0) return 'Overdue';
        if (daysRemaining === 0) return 'Due Today!';
        if (daysRemaining === 1) return '1 day left';
        return `${daysRemaining} days left`;
    };

    return (
        <div className="savings-goals-page">
            <div className="savings-goals-header">
                <h1>🎯 Savings Goals</h1>
                <button onClick={() => navigate('/add-savings-goal')} className="add-btn">
                    ➕ Add Goal
                </button>
            </div>

            {loading && <div className="loading">Loading savings goals...</div>}
            {error && <div className="error-message">{error}</div>}

            {!loading && goals.length === 0 && (
                <div className="empty-state">
                    <p>🎯 No savings goals yet</p>
                    <button onClick={() => navigate('/add-savings-goal')} className="add-first-btn">
                        Create Your First Goal
                    </button>
                </div>
            )}

            <div className="goals-grid">
                {goals.map(goal => (
                    <div
                        key={goal.id}
                        className={`goal-card ${goal.completed ? 'completed' : ''}`}
                    >
                        <div className="goal-header">
                            <h3>{goal.name}</h3>
                            <span className="goal-icon">
                                {getStatusIcon(goal.completed, goal.daysRemaining)}
                            </span>
                        </div>

                        <div className="goal-amounts">
                            <div className="amount-row">
                                <span className="amount-label">Saved</span>
                                <span className="amount-value current">
                                    {formatCurrency(goal.currentAmount)}
                                </span>
                            </div>
                            <div className="amount-row">
                                <span className="amount-label">Goal</span>
                                <span className="amount-value target">
                                    {formatCurrency(goal.targetAmount)}
                                </span>
                            </div>
                        </div>

                        <div className="goal-progress">
                            <div className="progress-bar">
                                <div
                                    className="progress-fill"
                                    style={{
                                        width: `${Math.min(goal.progressPercentage, 100)}%`,
                                        backgroundColor: getProgressColor(goal.progressPercentage)
                                    }}
                                ></div>
                            </div>
                            <span className="progress-text">{goal.progressPercentage}%</span>
                        </div>

                        <div className="goal-footer">
                            <div className="deadline">
                                <span className="deadline-label">📅 Deadline:</span>
                                <span className="deadline-date">{formatDate(goal.deadline)}</span>
                            </div>
                            <div className={`status ${goal.completed ? 'completed' : goal.daysRemaining < 0 ? 'overdue' : ''}`}>
                                {getStatusText(goal.completed, goal.daysRemaining)}
                            </div>
                        </div>

                        {goal.completed && (
                            <div className="completion-badge">
                                🎉 Goal Achieved!
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

export default SavingsGoals;