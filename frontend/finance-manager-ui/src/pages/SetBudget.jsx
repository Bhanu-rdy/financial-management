import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './SetBudget.css';

function SetBudget() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        categoryId: '',
        amount: '',
        month: new Date().getMonth() + 1,  // Current month (1-12)
        year: new Date().getFullYear(),
        alertThreshold: 80  // Default 80%
    });
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const months = [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
    ];

    const currentYear = new Date().getFullYear();
    const years = Array.from({ length: 5 }, (_, i) => currentYear + i);

    useEffect(() => {
        fetchExpenseCategories();
    }, []);

    const fetchExpenseCategories = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/categories/type/EXPENSE', {
                method: 'GET',
                credentials: 'include'
            });

            if (response.ok) {
                const data = await response.json();
                setCategories(data);
            }
        } catch (err) {
            console.error('Error fetching categories:', err);
        }
    };

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
            if (!formData.categoryId) {
                setError('Please select a category');
                setLoading(false);
                return;
            }

            if (!formData.amount || parseFloat(formData.amount) <= 0) {
                setError('Please enter a valid amount');
                setLoading(false);
                return;
            }

            // Prepare request body
            const requestBody = {
                categoryId: parseInt(formData.categoryId),
                amount: parseFloat(formData.amount),
                month: parseInt(formData.month),
                year: parseInt(formData.year),
                alertThreshold: parseInt(formData.alertThreshold)
            };

            // Send request
            const response = await fetch('http://localhost:8080/api/budgets', {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestBody)
            });

            const data = await response.json();

            if (response.ok) {
                setSuccess('✅ Budget set successfully!');

                // Clear form
                setFormData({
                    categoryId: '',
                    amount: '',
                    month: new Date().getMonth() + 1,
                    year: new Date().getFullYear(),
                    alertThreshold: 80
                });

                // Redirect to budgets page
                setTimeout(() => {
                    navigate('/budgets');
                }, 1500);
            } else {
                setError(data.error || 'Failed to set budget');
            }
        } catch (err) {
            setError('Failed to connect to server');
            console.error('Error:', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="set-budget-container">
            <div className="set-budget-card">
                <h2>Set Budget</h2>
                <p className="subtitle">Set a monthly spending limit for a category</p>

                {error && <div className="error-message">{error}</div>}
                {success && <div className="success-message">{success}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-row">
                        <div className="form-group">
                            <label>Month</label>
                            <select
                                name="month"
                                value={formData.month}
                                onChange={handleChange}
                                required
                            >
                                {months.map((month, index) => (
                                    <option key={index + 1} value={index + 1}>
                                        {month}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="form-group">
                            <label>Year</label>
                            <select
                                name="year"
                                value={formData.year}
                                onChange={handleChange}
                                required
                            >
                                {years.map(year => (
                                    <option key={year} value={year}>
                                        {year}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Expense Category</label>
                        <select
                            name="categoryId"
                            value={formData.categoryId}
                            onChange={handleChange}
                            required
                        >
                            <option value="">Select a category</option>
                            {categories.map(category => (
                                <option key={category.id} value={category.id}>
                                    {category.name}
                                </option>
                            ))}
                        </select>
                        {categories.length === 0 && (
                            <small className="hint-text">
                                No expense categories found.
                                <a href="/add-category"> Create one</a>
                            </small>
                        )}
                    </div>

                    <div className="form-group">
                        <label>Budget Amount ($)</label>
                        <input
                            type="number"
                            name="amount"
                            value={formData.amount}
                            onChange={handleChange}
                            placeholder="0.00"
                            step="0.01"
                            min="0.01"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Alert Threshold (%)</label>
                        <select
                            name="alertThreshold"
                            value={formData.alertThreshold}
                            onChange={handleChange}
                            required
                        >
                            <option value="50">50% - Alert when half spent</option>
                            <option value="75">75% - Alert at three quarters</option>
                            <option value="80">80% - Alert at 80%</option>
                            <option value="90">90% - Alert at 90%</option>
                            <option value="100">100% - Alert when exceeded</option>
                        </select>
                        <small className="hint-text">
                            You'll get an alert when spending reaches this threshold
                        </small>
                    </div>

                    <div className="button-group">
                        <button
                            type="submit"
                            disabled={loading}
                            className="submit-btn"
                        >
                            {loading ? 'Setting Budget...' : 'Set Budget'}
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

export default SetBudget;