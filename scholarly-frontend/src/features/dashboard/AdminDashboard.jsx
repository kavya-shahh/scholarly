import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import styles from './AdminDashboard.module.css';

export default function AdminDashboard() {
    const { user, logout } = useAuth();
    const [stats, setStats] = useState({
        totalStudents: 0,
        totalScholarships: 0,
        totalApplications: 0,
        totalAllocatedFunds: 0
    });
    const [scholarships, setScholarships] = useState([]);
    const [loadingStats, setLoadingStats] = useState(true);
    const [loadingScholarships, setLoadingScholarships] = useState(true);

    // Form states
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [amount, setAmount] = useState('');
    const [minGpa, setMinGpa] = useState('');
    const [deadline, setDeadline] = useState('');
    const [formError, setFormError] = useState('');
    const [formSuccess, setFormSuccess] = useState('');
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        fetchStats();
        fetchScholarships();
    }, []);

    const fetchStats = async () => {
        setLoadingStats(true);
        try {
            const response = await fetch('/api/v1/admin/stats', {
                credentials: 'include'
            });
            if (response.ok) {
                const data = await response.json();
                setStats(data);
            }
        } catch (err) {
            console.error('Failed to fetch admin stats:', err);
        } finally {
            setLoadingStats(false);
        }
    };

    const fetchScholarships = async () => {
        setLoadingScholarships(true);
        try {
            const response = await fetch('/api/v1/scholarships', {
                credentials: 'include'
            });
            if (response.ok) {
                const data = await response.json();
                setScholarships(data);
            }
        } catch (err) {
            console.error('Failed to load scholarships:', err);
        } finally {
            setLoadingScholarships(false);
        }
    };

    const handleCreateScholarship = async (e) => {
        e.preventDefault();
        setFormError('');
        setFormSuccess('');

        if (parseFloat(amount) <= 0) {
            setFormError('Amount must be a positive number.');
            return;
        }

        if (parseFloat(minGpa) < 0 || parseFloat(minGpa) > 10) {
            setFormError('Minimum GPA must be between 0.0 and 10.0.');
            return;
        }

        setSubmitting(true);
        try {
            // Standardize deadline string to ISO-8601 LocalDateTime format required by backend
            const formattedDeadline = `${deadline}T23:59:59`;
            const payload = {
                title,
                description,
                amount: parseFloat(amount),
                minGpa: parseFloat(minGpa),
                deadline: formattedDeadline
            };

            const response = await fetch('/api/v1/scholarships', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
                credentials: 'include'
            });

            if (response.status === 201) {
                setFormSuccess('Scholarship has been successfully created!');
                setTitle('');
                setDescription('');
                setAmount('');
                setMinGpa('');
                setDeadline('');
                // Reload statistics and registry list
                fetchStats();
                fetchScholarships();
            } else {
                const errData = await response.json();
                setFormError(errData.message || 'Failed to create scholarship.');
            }
        } catch (err) {
            setFormError('Network error. Please try again.');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className={styles.dashboardContainer}>
            <header className={styles.header}>
                <div className={styles.headerBrand}>
                    <span className={styles.logoBadge}>S</span>
                    <h1 className={styles.title}>scholarly <span className={styles.roleLabel}>Admin Portal</span></h1>
                </div>
                <div className={styles.profileSection}>
                    <div className={styles.profileInfo}>
                        <span className={styles.profileName}>{user?.firstName} {user?.lastName}</span>
                        <span className={styles.profileDetails}>{user?.email}</span>
                    </div>
                    <button onClick={logout} className={styles.logoutBtn}>Sign Out</button>
                </div>
            </header>

            <main className={styles.mainContent}>
                {/* 4-Card System Analytics metrics block */}
                <div className={styles.statsGrid}>
                    <div className={styles.statsCard}>
                        <div className={styles.statsTitle}>Students Registered</div>
                        <div className={styles.statsVal}>
                            {loadingStats ? <span className={styles.loader}></span> : stats.totalStudents}
                        </div>
                    </div>
                    <div className={styles.statsCard}>
                        <div className={styles.statsTitle}>Scholarships Offered</div>
                        <div className={styles.statsVal}>
                            {loadingStats ? <span className={styles.loader}></span> : stats.totalScholarships}
                        </div>
                    </div>
                    <div className={styles.statsCard}>
                        <div className={styles.statsTitle}>Applications Submitted</div>
                        <div className={styles.statsVal}>
                            {loadingStats ? <span className={styles.loader}></span> : stats.totalApplications}
                        </div>
                    </div>
                    <div className={styles.statsCard}>
                        <div className={styles.statsTitle}>Allocated Funding</div>
                        <div className={`${styles.statsVal} ${styles.allocatedText}`}>
                            {loadingStats ? (
                                <span className={styles.loader}></span>
                            ) : (
                                `₹${stats.totalAllocatedFunds.toLocaleString('en-IN', { maximumFractionDigits: 0 })}`
                            )}
                        </div>
                    </div>
                </div>

                <div className={styles.contentLayout}>
                    {/* Left Column: Create new Scholarship */}
                    <div className={styles.formContainer}>
                        <h2 className={styles.sectionTitle}>Deploy New Scholarship</h2>

                        {formError && <div className={styles.errorAlert}>{formError}</div>}
                        {formSuccess && <div className={styles.successAlert}>{formSuccess}</div>}

                        <form onSubmit={handleCreateScholarship} className={styles.form}>
                            <div className={styles.formGroup}>
                                <label className={styles.label}>Scholarship Title</label>
                                <input
                                    type="text"
                                    className={styles.input}
                                    placeholder="e.g. Aditya Birla Merit Scholarship"
                                    value={title}
                                    onChange={(e) => setTitle(e.target.value)}
                                    required
                                />
                            </div>

                            <div className={styles.formGroup}>
                                <label className={styles.label}>Description</label>
                                <textarea
                                    rows="4"
                                    className={styles.textarea}
                                    placeholder="Provide detailed scholarship requirements, coverage, eligibility criteria, and instructions..."
                                    value={description}
                                    onChange={(e) => setDescription(e.target.value)}
                                    required
                                />
                            </div>

                            <div className={styles.formGrid}>
                                <div className={styles.formGroup}>
                                    <label className={styles.label}>Amount (₹ INR)</label>
                                    <input
                                        type="number"
                                        className={styles.input}
                                        placeholder="50000"
                                        value={amount}
                                        onChange={(e) => setAmount(e.target.value)}
                                        required
                                    />
                                </div>
                                <div className={styles.formGroup}>
                                    <label className={styles.label}>Min CGPA Requirement</label>
                                    <input
                                        type="number"
                                        step="0.01"
                                        min="0.00"
                                        max="10.00"
                                        className={styles.input}
                                        placeholder="8.50"
                                        value={minGpa}
                                        onChange={(e) => setMinGpa(e.target.value)}
                                        required
                                    />
                                </div>
                            </div>

                            <div className={styles.formGroup}>
                                <label className={styles.label}>Application Deadline</label>
                                <input
                                    type="date"
                                    className={styles.input}
                                    value={deadline}
                                    onChange={(e) => setDeadline(e.target.value)}
                                    required
                                />
                            </div>

                            <button
                                type="submit"
                                disabled={submitting}
                                className={styles.submitBtn}
                            >
                                {submitting ? 'Deploying...' : 'Deploy Scholarship'}
                            </button>
                        </form>
                    </div>

                    {/* Right Column: Scholarship registry */}
                    <div className={styles.registryContainer}>
                        <h2 className={styles.sectionTitle}>Active Scholarship Registry</h2>

                        {loadingScholarships ? (
                            <div className={styles.spinnerContainer}>
                                <div className={styles.spinner}></div>
                            </div>
                        ) : scholarships.length === 0 ? (
                            <div className={styles.emptyState}>No scholarships registered in the system.</div>
                        ) : (
                            <div className={styles.scholarshipList}>
                                {scholarships.map(s => (
                                    <div key={s.id} className={styles.scholarshipCard}>
                                        <div className={styles.cardHeader}>
                                            <h3 className={styles.cardTitle}>{s.title}</h3>
                                            <span className={styles.cardAmount}>₹{s.amount.toLocaleString('en-IN', { maximumFractionDigits: 0 })}</span>
                                        </div>
                                        <p className={styles.cardDesc}>{s.description}</p>
                                        <div className={styles.cardMeta}>
                                            <div><strong>Min CGPA:</strong> {s.minGpa}</div>
                                            <div><strong>Deadline:</strong> {new Date(s.deadline).toLocaleDateString('en-GB')}</div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            </main>
        </div>
    );
}
