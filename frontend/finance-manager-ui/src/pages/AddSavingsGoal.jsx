import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './AddSavingsGoal.css';

function AddSavingsGoal() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        targetAmount: '',
        currentAmount: '0',
        deadline: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
        setError('');
        setSuccess('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');

        try {
            // Validate
            if (!formData.name) {
                setError('Please enter a goal name');
                setLoading(false);
                return;
            }

            if (!formData.targetAmount || parseFloat(formData.targetAmount) <= 0) {
                setError('Please enter a valid target amount');
                setLoading(false);
                return;
            }

            if (parseFloat(formData.currentAmount) < 0) {
                setError('Current amount cannot be negative');
                setLoading(false);
                return;
            }

            if (!formData.deadline) {
                setError('Please select a deadline');
                setLoading(false);
                return;
            }

            // Prepare request body
            const requestBody = {
                name: formData.name,
                targetAmount: parseFloat(formData.targetAmount),
                currentAmount: parseFloat(formData.currentAmount),
                deadline: formData.deadline
            };

            // Send request
            const response = await fetch('https://financial-management-e8o9.onrender.com/api/savings', {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestBody)
            });

            const data = await response.json();

            if (response.ok) {
                setSuccess('✅ Savings goal created successfully!');

                // Clear form
                setFormData({
                    name: '',
                    targetAmount: '',
                    currentAmount: '0',
                    deadline: ''
                });

                // Redirect to savings goals page
                setTimeout(() => {
                    navigate('/savings-goals');
                }, 1500);
            } else {
                setError(data.error || 'Failed to create savings goal');
            }
        } catch (err) {
            setError('Failed to connect to server');
            console.error('Error:', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="add-savings-goal-container">
            <div className="add-savings-goal-card">
                <h2>🎯 Add Savings Goal</h2>
                <p className="subtitle">Set a target and track your progress</p>

                {error && <div className="error-message">{error}</div>}
                {success && <div className="success-message">{success}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Goal Name</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            placeholder="e.g., Vacation, New Car, Emergency Fund"
                            autoFocus
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Target Amount ($)</label>
                        <input
                            type="number"
                            name="targetAmount"
                            value={formData.targetAmount}
                            onChange={handleChange}
                            placeholder="0.00"
                            step="0.01"
                            min="0.01"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Current Amount ($)</label>
                        <input
                            type="number"
                            name="currentAmount"
                            value={formData.currentAmount}
                            onChange={handleChange}
                            placeholder="0.00"
                            step="0.01"
                            min="0"
                        />
                        <small className="hint-text">
                            How much have you already saved?
                        </small>
                    </div>

                    <div className="form-group">
                        <label>Deadline</label>
                        <input
                            type="date"
                            name="deadline"
                            value={formData.deadline}
                            onChange={handleChange}
                            min={new Date().toISOString().split('T')[0]}
                            required
                        />
                    </div>

                    <div className="button-group">
                        <button
                            type="submit"
                            disabled={loading}
                            className="submit-btn"
                        >
                            {loading ? 'Creating...' : 'Create Goal'}
                        </button>

                        <button
                            type="button"
                            onClick={() => navigate('/dashboard')}
                            className="cancel-btn"
                        >
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default AddSavingsGoal;