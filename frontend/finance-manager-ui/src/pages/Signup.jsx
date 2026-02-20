import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './Signup.css';

function Signup() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        userName: '',
        email: '',
        password: '',
        firstName: '',
        lastName: ''
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
            // Step 1: Validate all fields are filled
                    if (!formData.userName || !formData.email || !formData.password ||
                        !formData.firstName || !formData.lastName) {
                        setError('Please fill in all fields');
                        setLoading(false);
                        return;
                    }

                    // Step 2: Validate password length (minimum 8 characters)
                    if (formData.password.length < 8) {
                        setError('Password must be at least 8 characters long');
                        setLoading(false);
                        return;
                    }

                    // Step 3: Validate email format (basic check)
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    if (!emailRegex.test(formData.email)) {
                        setError('Please enter a valid email address');
                        setLoading(false);
                        return;
                    }

                    //step 4: Check userName availability (API - call)
                    const userNameCheckResponse = await fetch("https://financial-management-e8o9.onrender.com/api/auth/check-username", {
                        method: "POST",
                        headers: {
                            "Content-Type" : "application/json"
                            },
                        body: JSON.stringify({ userName: formData.userName})
                    });

                const userNameCheckData = await userNameCheckResponse.json();

                if (!userNameCheckResponse.ok || !userNameCheckData.available) {
                            setError(userNameCheckData.message || 'Username is already taken');
                            setLoading(false);
                            return;
                        }

                    // Step 5: Check email availability (NEW - API CALL)
                            const emailCheckResponse = await fetch("https://financial-management-e8o9.onrender.com/api/auth/check-email", {
                                method: "POST",
                                headers: {
                                    "Content-Type": "application/json"
                                },
                                body: JSON.stringify({ email: formData.email })
                            });

                            const emailCheckData = await emailCheckResponse.json();

                            if (!emailCheckResponse.ok || !emailCheckData.available) {
                                setError(emailCheckData.message || 'Email is already registered');
                                setLoading(false);
                                return;
                            }
            // Step 6: All validations passed - Store data in localStorage
                    localStorage.setItem('pendingVerificationEmail', JSON.stringify(formData));

                    setSuccess('Username available! Redirecting to email verification...');

                    // Step 6: Navigate to OTP verification page
                    setTimeout(() => {
                        navigate('/verify-otp');
                    }, 1000);

                } catch (err) {
                    setError('Failed to connect to server. Please try again.');
                    console.error('Error:', err);
                } finally {
                    setLoading(false);
                }

    };

    return (
        <div className="signup-container">
            <div className="signup-card">
                <h2>Create Account</h2>
                <p className="subtitle">Join Finance Manager today</p>

                {error && <div className="error-message">{error}</div>}
                {success && <div className="success-message">{success}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Username</label>
                        <input
                            type="text"
                            name="userName"
                            value={formData.userName}
                            onChange={handleChange}
                            required
                            placeholder="Choose a username"
                        />
                    </div>

                    <div className="form-group">
                        <label>Email</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                            placeholder="your@email.com"
                        />
                    </div>

                    <div className="form-group">
                        <label>Password</label>
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                            placeholder="Choose a strong password"
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>First Name</label>
                            <input
                                type="text"
                                name="firstName"
                                value={formData.firstName}
                                onChange={handleChange}
                                required
                                placeholder="First name"
                            />
                        </div>

                        <div className="form-group">
                            <label>Last Name</label>
                            <input
                                type="text"
                                name="lastName"
                                value={formData.lastName}
                                onChange={handleChange}
                                required
                                placeholder="Last name"
                            />
                        </div>
                    </div>

                    <button type="submit" disabled={loading} className="submit-btn">
                        {loading ? 'Creating Account...' : 'Sign Up'}
                    </button>
                </form>

                <p className="login-link">
                    Already have an account? <Link to="/login">Login</Link>
                </p>
            </div>
        </div>
    );
}

export default Signup;

