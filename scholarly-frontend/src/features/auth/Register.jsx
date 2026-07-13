import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import styles from './Auth.module.css';

const Register = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [role, setRole] = useState('STUDENT');
    
    // Student specific fields
    const [gpa, setGpa] = useState('');
    const [department, setDepartment] = useState('');
    const [enrollmentNumber, setEnrollmentNumber] = useState('');
    
    const [formError, setFormError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [submitting, setSubmitting] = useState(false);

    const { register } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setFormError('');
        setSuccessMessage('');
        setSubmitting(true);

        const payload = {
            email,
            password,
            firstName,
            lastName,
            role,
            gpa: role === 'STUDENT' ? parseFloat(gpa) : null,
            department: role === 'STUDENT' ? department : null,
            enrollmentNumber: role === 'STUDENT' ? enrollmentNumber : null
        };

        try {
            await register(payload);
            setSuccessMessage('Registration successful! Redirecting to login...');
            setTimeout(() => {
                navigate('/login');
            }, 2000);
        } catch (err) {
            setFormError(err.message || 'Registration failed. Please check inputs.');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className={styles.authContainer}>
            <div className={styles.authCard}>
                <h1 className={styles.title}>Scholarly</h1>
                <p className={styles.subtitle}>Create your university profile</p>

                {formError && <div className={styles.errorAlert}>{formError}</div>}
                {successMessage && <div style={{ background: 'rgba(0,179,126,0.1)', borderColor: '#00b37e', color: '#00b37e' }} className={styles.errorAlert}>{successMessage}</div>}

                <form onSubmit={handleSubmit}>
                    <div className={styles.studentGrid}>
                        <div className={styles.formGroup}>
                            <label className={styles.label}>First Name</label>
                            <input
                                type="text"
                                className={styles.input}
                                placeholder="Pinky"
                                value={firstName}
                                onChange={(e) => setFirstName(e.target.value)}
                                required
                            />
                        </div>
                        <div className={styles.formGroup}>
                            <label className={styles.label}>Last Name</label>
                            <input
                                type="text"
                                className={styles.input}
                                placeholder="Shah"
                                value={lastName}
                                onChange={(e) => setLastName(e.target.value)}
                                required
                            />
                        </div>
                    </div>

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
                            placeholder="Min 6 characters"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    <div className={styles.formGroup}>
                        <label className={styles.label}>Register As</label>
                        <select
                            className={styles.select}
                            value={role}
                            onChange={(e) => setRole(e.target.value)}
                            required
                        >
                            <option value="STUDENT">Student</option>
                            <option value="FACULTY">Faculty Member</option>
                            <option value="ADMIN">Administrator</option>
                        </select>
                    </div>

                    {role === 'STUDENT' && (
                        <div>
                            <div className={styles.formGroup}>
                                <label className={styles.label}>Enrollment Number</label>
                                <input
                                    type="text"
                                    className={styles.input}
                                    placeholder="e.g. CS2026001"
                                    value={enrollmentNumber}
                                    onChange={(e) => setEnrollmentNumber(e.target.value)}
                                    required={role === 'STUDENT'}
                                />
                            </div>

                            <div className={styles.studentGrid}>
                                <div className={styles.formGroup}>
                                    <label className={styles.label}>Current GPA</label>
                                    <input
                                        type="number"
                                        step="0.01"
                                        min="0.00"
                                        max="10.00"
                                        className={styles.input}
                                        placeholder="GPA (out of 10.0)"
                                        value={gpa}
                                        onChange={(e) => setGpa(e.target.value)}
                                        required={role === 'STUDENT'}
                                    />
                                </div>
                                <div className={styles.formGroup}>
                                    <label className={styles.label}>Department</label>
                                    <input
                                        type="text"
                                        className={styles.input}
                                        placeholder="e.g. CS / IT"
                                        value={department}
                                        onChange={(e) => setDepartment(e.target.value)}
                                        required={role === 'STUDENT'}
                                    />
                                </div>
                            </div>
                        </div>
                    )}

                    <button type="submit" className={styles.submitBtn} disabled={submitting}>
                        {submitting ? 'Registering...' : 'Register'}
                    </button>
                </form>

                <p className={styles.toggleText}>
                    Already have an account?{' '}
                    <Link to="/login" className={styles.toggleLink}>
                        Sign in here
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default Register;
