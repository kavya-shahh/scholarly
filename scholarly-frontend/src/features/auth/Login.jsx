import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import styles from './Auth.module.css';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [formError, setFormError] = useState('');
    const [submitting, setSubmitting] = useState(false);

    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setFormError('');
        setSubmitting(true);

        try {
            const user = await login(email, password);

            if (user.role === 'STUDENT') {
                navigate('/student/dashboard');
            } else if (user.role === 'FACULTY') {
                navigate('/faculty/dashboard');
            } else if (user.role === 'ADMIN') {
                navigate('/admin/dashboard');
            } else {
                navigate('/');
            }
        } catch (err) {
            setFormError(err.message || 'Invalid email or password');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className={styles.authContainer}>
            <div className={styles.authCard}>
                <img src="/y6lk001.svg" alt="Scholarly Logo" style={{ width: '135px', height: 'auto', marginBottom: '1rem' }} />

                <h1 className={styles.title}>Scholarly</h1>
                <p className={styles.subtitle}>Sign in to access your portal</p>

                {formError && <div className={styles.errorAlert}>{formError}</div>}

                <form onSubmit={handleSubmit}>
                    <div className={styles.formGroup}>
                        <label className={styles.label}>Email Address</label>
                        <input
                            type="email"
                            className={styles.input}
                            placeholder="name@university.edu"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>

                    <div className={styles.formGroup}>
                        <label className={styles.label}>Password</label>
                        <input
                            type="password"
                            className={styles.input}
                            placeholder="••••••••"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    <button type="submit" className={styles.submitBtn} disabled={submitting}>
                        {submitting ? 'Signing in...' : 'Sign In'}
                    </button>
                </form>

                <p className={styles.toggleText}>
                    New to Scholarly?{' '}
                    <Link to="/register" className={styles.toggleLink}>
                        Register here
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default Login;
