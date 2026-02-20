import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './AddCategory.css';

function AddCategory() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        type: 'EXPENSE'  // Default to EXPENSE
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
                setError('Please enter category name');
                setLoading(false);
                return;
            }

            // Send request with JWT token
            const response = await fetch('https://financial-management-e8o9.onrender.com/api/categories', {
                method: 'POST',
                credentials: 'include', // to send cookies
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });


            const data = await response.json();

            if (response.ok) {
                setSuccess('✅ Category created successfully!');

                // Clear form
                setFormData({ name: '', type: 'EXPENSE' });

                // Redirect to categories page
                setTimeout(() => {
                    navigate('/dashboard');
                }, 3000);
            } else {
                setError(data.error || 'Failed to create category');
            }
        } catch (err) {
            setError('Failed to connect to server');
            console.error('Error:', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="add-category-container">
            <div className="add-category-card">
                <h2>Add New Category</h2>
                <p className="subtitle">Create a category to organize your transactions</p>

                {error && <div className="error-message">{error}</div>}
                {success && <div className="success-message">{success}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Category Name</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            placeholder="e.g., Groceries, Salary, Rent"
                            autoFocus
                        />
                    </div>

                    <div className="form-group">
                        <label>Type</label>
                        <select
                            name="type"
                            value={formData.type}
                            onChange={handleChange}
                        >
                            <option value="INCOME">Income</option>
                            <option value="EXPENSE">Expense</option>
                        </select>
                    </div>

                    <div className="button-group">
                        <button
                            type="submit"
                            disabled={loading}
                            className="submit-btn"
                        >
                            {loading ? 'Creating...' : 'Create Category'}
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

export default AddCategory;