import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './OTPVerification.css';

function OTPVerification() {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [otp, setOtp] = useState('');
    const [otpSent, setOtpSent] = useState(false);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [pendingSignup, setPendingSignup] = useState(null);

    // On component mount, get pending signup data
    useEffect(() => {
        const storedSignup = localStorage.getItem('pendingVerificationEmail');

        if (!storedSignup) {
            navigate('/signup');
            return;
        }

        let parsedSignup;
        try {
            parsedSignup = JSON.parse(storedSignup);
        } catch (err) {
            localStorage.removeItem('pendingVerificationEmail');
            navigate('/signup');
            return;
        }

        if (!parsedSignup.email) {
            localStorage.removeItem('pendingVerificationEmail');
            navigate('/signup');
            return;
        }

        setPendingSignup(parsedSignup);
        setEmail(parsedSignup.email);
    }, [navigate]);

    // Send OTP when user clicks button
    const handleSendOTP = async () => {
        setError('');
        setMessage('');
        setLoading(true);

        try {
            const response = await fetch('https://financial-management-e8o9.onrender.com/api/otp/send', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email: email })
            });
            console.log(response);

            const data = await response.json();
           console.log(data);
            if (response.ok && data.success) {
                setOtpSent(true);
                setMessage('✅ OTP sent successfully! Check your email inbox.');
            } else {
                setError(data.message || 'Failed to send OTP');
            }
        } catch (err) {
            setError('Failed to connect to server');
        } finally {
            setLoading(false);
        }
    };

    // Verify OTP and create account
    const handleVerifyOTP = async () => {
        setError('');
        setMessage('');
        setLoading(true);

        try {
            // Step 1: Verify OTP
            const otpResponse = await fetch('https://financial-management-e8o9.onrender.com/api/otp/verify', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    email: email,
                    otp: otp
                })
            });

            const otpData = await otpResponse.json();

            if (otpResponse.ok && otpData.success) {
                // Step 2: OTP verified, now create account
                if (!pendingSignup) {
                    setError('Missing signup details. Please sign up again.');
                    setLoading(false);
                    return;
                }

                const registerResponse = await fetch('https://financial-management-e8o9.onrender.com/api/auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(pendingSignup)
                });


                const registerData = await registerResponse.json();

                if (registerResponse.ok) {
                    // Success! Account created
                    setMessage('✅ Email verified! Account created successfully!');

                    // Clear pending signup
                    localStorage.removeItem('pendingVerificationEmail');

                    // Redirect to dashboard
                    setTimeout(() => {
                        navigate('/login');
                    }, 2000);
                } else {
                    setError(registerData.error || 'Failed to create account');
                }
            } else {
                setError(otpData.message || 'Invalid or expired OTP');
            }
        } catch (err) {
            setError('Failed to verify OTP');
        } finally {
            setLoading(false);
        }
    };

    // Resend OTP
    const handleResendOTP = () => {
        handleSendOTP();

    };

    return (
        <div className="otp-container">
            <div className="otp-box">
                <div className="otp-header">
                    <h1>📧 Email Verification</h1>
                    <p className="subtitle">Verify your email to complete registration</p>
                </div>

                {message && <div className="success-message">{message}</div>}
                {error && <div className="error-message">{error}</div>}

                <div className="email-display">
                    <label>Email Address</label>
                    <input
                        type="email"
                        value={email}
                        disabled
                        className="email-input"
                    />
                </div>

                {!otpSent ? (
                    // STEP 1: Send OTP Button
                    <div className="otp-section">
                        <p className="info-text">
                            Click the button below to receive a 6-digit verification code
                        </p>
                        <button
                            onClick={handleSendOTP}
                            disabled={loading}
                            className="otp-btn primary-btn"
                        >
                            {loading ? 'Sending...' : 'Send OTP'}
                        </button>
                    </div>
                ) : (
                    // STEP 2: Enter OTP and Verify
                    <div className="otp-section">
                        <div className="otp-input-group">
                            <label>Enter Verification Code</label>
                            <input
                                type="text"
                                value={otp}
                                onChange={(e) => setOtp(e.target.value.replace(/\D/g, ''))}
                                placeholder="Enter 6-digit code"
                                maxLength="6"
                                className="otp-input"
                                autoFocus
                            />
                            <p className="help-text">Code expires in 5 minutes</p>
                        </div>

                        <button
                            onClick={handleVerifyOTP}
                            disabled={loading || otp.length !== 6}
                            className="otp-btn primary-btn"
                        >
                            {loading ? 'Verifying...' : 'Verify OTP'}
                        </button>

                        <button
                            onClick={handleResendOTP}
                            disabled={loading}
                            className="otp-btn secondary-btn"
                        >
                            Resend OTP
                        </button>
                    </div>
                )}

                <div className="back-link">
                    <a href="#" onClick={(e) => {
                        e.preventDefault();
                        localStorage.removeItem('pendingSignup');
                        navigate('/signup');
                    }}>
                        ← Back to Signup
                    </a>
                </div>
            </div>
        </div>
    );
}

export default OTPVerification;