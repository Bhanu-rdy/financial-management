import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './AddTransaction.css';

function AddTransaction() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        type: 'EXPENSE',
        categoryId: '',
        amount: '',
        description: '',
        transactionDate: new Date().toISOString().split('T')[0]  // Today's date
    });
    const [categories, setCategories] = useState([]);
    const [filteredCategories, setFilteredCategories] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // Fetch categories on mount
    useEffect(() => {
        fetchCategories();
    }, []);

    // Filter categories when type changes
    useEffect(() => {
        if (categories.length > 0) {
            const filtered = categories.filter(cat => cat.type === formData.type);
            setFilteredCategories(filtered);

            // Reset categoryId when type changes
            if (formData.categoryId) {
                const selectedCategory = categories.find(cat => cat.id === parseInt(formData.categoryId));
                if (selectedCategory && selectedCategory.type !== formData.type) {
                    setFormData(prev => ({ ...prev, categoryId: '' }));
                }
            }
        }
    }, [formData.type, categories]);

    const fetchCategories = async () => {
        try {
            const response = await fetch('https://financial-management-e8o9.onrender.com/api/categories', {
                method: 'GET',
                credentials: 'include'
            });

            if (response.ok) {
                const data = await response.json();
                setCategories(data);

                console.log(data);



                // Set initial filtered categories
                const filtered = data.filter(cat => cat.type === formData.type);
                setFilteredCategories(filtered);
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

            if (!formData.transactionDate) {
                setError('Please select a date');
                setLoading(false);
                return;
            }

            // Prepare request body
            const requestBody = {
                categoryId: parseInt(formData.categoryId),
                amount: parseFloat(formData.amount),
                type: formData.type,
                description: formData.description || null,
                transactionDate: formData.transactionDate
            };

            // Send request
            const response = await fetch('https://financial-management-e8o9.onrender.com/api/transactions', {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestBody)
            });

            const data = await response.json();

            if (response.ok) {
                setSuccess('✅ Transaction added successfully!');

                // Clear form
                setFormData({
                    type: 'EXPENSE',
                    categoryId: '',
                    amount: '',
                    description: '',
                    transactionDate: new Date().toISOString().split('T')[0]
                });

                // Redirect to transactions page
                setTimeout(() => {
                    navigate('/dashboard');
                }, 1500);
            } else {
                setError(data.error || 'Failed to add transaction');
            }
        } catch (err) {
            setError('Failed to connect to server');
            console.error('Error:', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="add-transaction-container">
            <div className="add-transaction-card">
                <h2>Add New Transaction</h2>
                <p className="subtitle">Record your income or expense</p>

                {error && <div className="error-message">{error}</div>}
                {success && <div className="success-message">{success}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Type</label>
                        <select
                            name="type"
                            value={formData.type}
                            onChange={handleChange}
                            className="type-select"
                        >
                            <option value="INCOME">💰 Income</option>
                            <option value="EXPENSE">💸 Expense</option>
                        </select>
                    </div>

                    <div className="form-group">
                        <label>Category</label>
                        <select
                            name="categoryId"
                            value={formData.categoryId}
                            onChange={handleChange}
                            required
                        >
                            <option value="">Select a category</option>
                            {filteredCategories.map(category => (
                                <option key={category.id} value={category.id}>
                                    {category.name}
                                </option>
                            ))}
                        </select>
                        {filteredCategories.length === 0 && (
                            <small className="hint-text">
                                No {formData.type.toLowerCase()} categories found.
                                <a href="/add-category"> Create one</a>
                            </small>
                        )}
                    </div>

                    <div className="form-group">
                        <label>Amount ($)</label>
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
                        <label>Description (Optional)</label>
                        <textarea
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            placeholder="Add a note about this transaction..."
                            rows="3"
                        />
                    </div>

                    <div className="form-group">
                        <label>Date</label>
                        <input
                            type="date"
                            name="transactionDate"
                            value={formData.transactionDate}
                            onChange={handleChange}
                            max={new Date().toISOString().split('T')[0]}
                            required
                        />
                    </div>

                    <div className="button-group">
                        <button
                            type="submit"
                            disabled={loading}
                            className="submit-btn"
                        >
                            {loading ? 'Saving...' : 'Add Transaction'}
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

export default AddTransaction;