import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import styles from './StudentDashboard.module.css';

const StudentDashboard = () => {
    const { user, logout } = useAuth();
    const [scholarships, setScholarships] = useState([]);
    const [applications, setApplications] = useState([]);
    const [activeTab, setActiveTab] = useState('scholarships');
    const [loading, setLoading] = useState(true);
    
    // Modal & Apply State
    const [showApplyModal, setShowApplyModal] = useState(false);
    const [selectedScholarship, setSelectedScholarship] = useState(null);
    const [gpaEntered, setGpaEntered] = useState(user?.gpa || '');
    const [transcriptFile, setTranscriptFile] = useState(null);
    const [modalError, setModalError] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');

    useEffect(() => {
        fetchDashboardData();
    }, []);

    const fetchDashboardData = async () => {
        setLoading(true);
        try {
            // Parallel fetch of scholarships and current student applications
            const [scholarshipsRes, applicationsRes] = await Promise.all([
                fetch('/api/v1/scholarships', { credentials: 'include' }),
                fetch('/api/v1/applications/me', { credentials: 'include' })
            ]);

            if (scholarshipsRes.ok) {
                const scholarshipsData = await scholarshipsRes.json();
                setScholarships(scholarshipsData);
            }
            if (applicationsRes.ok) {
                const applicationsData = await applicationsRes.json();
                setApplications(applicationsData);
            }
        } catch (err) {
            console.error("Failed to load dashboard data:", err);
        } finally {
            setLoading(false);
        }
    };

    const handleApplyClick = (scholarship) => {
        if (applications.length > 0) {
            alert('A student is eligible for only one scholarship at a time.');
            return;
        }
        setSelectedScholarship(scholarship);
        setGpaEntered(user?.gpa || '');
        setTranscriptFile(null);
        setModalError('');
        setSuccessMessage('');
        setShowApplyModal(true);
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            // Strictly enforce PDF file format
            if (file.type !== 'application/pdf') {
                setModalError('Only PDF files are allowed.');
                setTranscriptFile(null);
                return;
            }
            // Enforce max 5MB file size constraint
            const maxSizeBytes = 5 * 1024 * 1024;
            if (file.size > maxSizeBytes) {
                setModalError('File size exceeds the 5MB limit.');
                setTranscriptFile(null);
                return;
            }
            setModalError('');
            setTranscriptFile(file);
        }
    };

    const handleApplySubmit = async (e) => {
        e.preventDefault();
        if (!transcriptFile) {
            setModalError('Please upload your transcript document.');
            return;
        }

        setModalError('');
        setSubmitting(true);

        const formData = new FormData();
        formData.append('scholarshipId', selectedScholarship.id);
        formData.append('gpaEntered', gpaEntered);
        formData.append('file', transcriptFile);

        try {
            const response = await fetch('/api/v1/applications', {
                method: 'POST',
                body: formData,
                credentials: 'include'
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.message || 'Failed to submit application.');
            }

            setSuccessMessage('Application submitted successfully!');
            setTimeout(async () => {
                setShowApplyModal(false);
                await fetchDashboardData(); // Refresh datasets
            }, 1500);
        } catch (err) {
            setModalError(err.message);
        } finally {
            setSubmitting(false);
        }
    };

    const hasApplied = (scholarshipId) => {
        return applications.some(app => app.scholarshipId === scholarshipId);
    };

    const getStatusStyle = (status) => {
        switch (status) {
            case 'APPROVED': return styles.statusApproved;
            case 'REJECTED': return styles.statusRejected;
            case 'PENDING_VERIFICATION': return styles.statusPending;
            default: return styles.statusSubmitted;
        }
    };

    const formatStatusText = (status) => {
        return status.replace('_', ' ');
    };

    return (
        <div className={styles.dashboardContainer}>
            {/* Header Component */}
            <header className={styles.header}>
                <div className={styles.headerBrand}>
                    <img src="/y6lk001.svg" alt="Scholarly Logo" className={styles.logo} />
                    <h1 className={styles.title}>Scholarly</h1>
                </div>
                <div className={styles.profileSection}>
                    <div className={styles.profileInfo}>
                        <span className={styles.profileName}>{user?.firstName} {user?.lastName}</span>
                        <span className={styles.profileDetails}>GPA: {user?.gpa} | {user?.department}</span>
                    </div>
                    <button onClick={logout} className={styles.logoutBtn}>Sign Out</button>
                </div>
            </header>

            {/* Sub-Panel Content Layout */}
            <main className={styles.mainContent}>
                <div className={styles.tabsSection}>
                    <button 
                        className={`${styles.tabBtn} ${activeTab === 'scholarships' ? styles.activeTab : ''}`}
                        onClick={() => setActiveTab('scholarships')}
                    >
                        Browse Scholarships
                    </button>
                    <button 
                        className={`${styles.tabBtn} ${activeTab === 'applications' ? styles.activeTab : ''}`}
                        onClick={() => setActiveTab('applications')}
                    >
                        My Applications ({applications.length})
                    </button>
                </div>

                {loading ? (
                    <div className={styles.loaderContainer}>
                        <div className={styles.spinner}></div>
                    </div>
                ) : (
                    <div>
                        {/* Tab 1: Browse Scholarships */}
                        {activeTab === 'scholarships' && (
                            <div>
                                {applications.length > 0 && (
                                    <div style={{
                                        background: 'rgba(234, 179, 8, 0.1)',
                                        border: '1px solid rgba(234, 179, 8, 0.2)',
                                        borderRadius: '6px',
                                        padding: '12px 20px',
                                        color: '#eab308',
                                        fontSize: '14px',
                                        fontWeight: '500',
                                        marginBottom: '24px',
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: '10px'
                                    }}>
                                        ⚠️ Note: You have already submitted an application. A student is eligible for only one scholarship at a time.
                                    </div>
                                )}
                                <div className={styles.grid}>
                                    {scholarships.length === 0 ? (
                                        <div className={styles.emptyState}>No active scholarships available at the moment.</div>
                                    ) : (
                                        scholarships.map(s => {
                                            const ineligible = user?.gpa < s.minGpa;
                                            const alreadyApplied = hasApplied(s.id);

                                            return (
                                                <div key={s.id} className={styles.card}>
                                                    <div className={styles.cardHeader}>
                                                        <h3 className={styles.cardTitle}>{s.title}</h3>
                                                        <span className={styles.amountBadge}>₹{s.amount.toLocaleString('en-IN', { maximumFractionDigits: 0 })}</span>
                                                    </div>
                                                    <p className={styles.cardDesc}>{s.description}</p>
                                                    
                                                    <div className={styles.cardMeta}>
                                                        <div>
                                                            <strong>Deadline:</strong> {new Date(s.deadline).toLocaleDateString('en-GB')}
                                                        </div>
                                                        <div>
                                                            <strong>Min GPA:</strong> {s.minGpa}
                                                        </div>
                                                    </div>

                                                    <div className={styles.cardActions}>
                                                        {alreadyApplied ? (
                                                            <button className={styles.appliedBtn} disabled>Applied</button>
                                                        ) : applications.length > 0 ? (
                                                            <button className={styles.appliedBtn} style={{ borderColor: '#636370', color: '#636370' }} disabled>Applied to Another</button>
                                                        ) : ineligible ? (
                                                            <div className={styles.ineligibleContainer}>
                                                                <span className={styles.ineligibleBadge}>GPA {s.minGpa} required</span>
                                                                <button className={styles.applyBtn} disabled>Apply</button>
                                                            </div>
                                                        ) : (
                                                            <button onClick={() => handleApplyClick(s)} className={styles.applyBtn}>Apply Now</button>
                                                        )}
                                                    </div>
                                                </div>
                                            );
                                        })
                                    )}
                                </div>
                            </div>
                        )}

                        {/* Tab 2: Application Tracker */}
                        {activeTab === 'applications' && (
                            <div className={styles.grid}>
                                {applications.length === 0 ? (
                                    <div className={styles.emptyState}>You haven't submitted any scholarship applications yet.</div>
                                ) : (
                                    applications.map(app => (
                                        <div key={app.id} className={styles.card}>
                                            <div className={styles.cardHeader}>
                                                <h3 className={styles.cardTitle}>{app.scholarshipTitle}</h3>
                                                <span className={`${styles.statusBadge} ${getStatusStyle(app.status)}`}>
                                                    {formatStatusText(app.status)}
                                                </span>
                                            </div>
                                            
                                            <div className={styles.appMeta}>
                                                <div><strong>Applied On:</strong> {new Date(app.appliedAt).toLocaleDateString('en-GB')}</div>
                                                <div><strong>GPA Submitted:</strong> {app.gpaEntered}</div>
                                                {app.gpaExtracted && (
                                                    <div><strong>GPA Extracted (OCR):</strong> {app.gpaExtracted}</div>
                                                )}
                                                <div style={{ marginTop: '10px' }}>
                                                    <a href={`http://localhost:8080/api${app.transcriptUrl}`} target="_blank" rel="noopener noreferrer" className={styles.viewLink}>
                                                        View Transcript document
                                                    </a>
                                                </div>
                                            </div>

                                            {app.facultyComments && (
                                                <div className={styles.commentBox}>
                                                    <strong>Faculty Review Remarks:</strong>
                                                    <p>{app.facultyComments}</p>
                                                </div>
                                            )}
                                        </div>
                                    ))
                                )}
                            </div>
                        )}
                    </div>
                )}
            </main>

            {/* File Upload Application Modal */}
            {showApplyModal && selectedScholarship && (
                <div className={styles.modalOverlay}>
                    <div className={styles.modalContent}>
                        <h2 className={styles.modalTitle}>Apply for {selectedScholarship.title}</h2>
                        <p className={styles.modalSubtitle}>Please attach your official transcript to proceed</p>

                        {modalError && <div className={styles.modalError}>{modalError}</div>}
                        {successMessage && <div className={styles.modalSuccess}>{successMessage}</div>}

                        <form onSubmit={handleApplySubmit}>
                            <div className={styles.modalFormGroup}>
                                <label className={styles.modalLabel}>Confirm GPA</label>
                                <input
                                    type="number"
                                    step="0.01"
                                    className={styles.modalInput}
                                    value={gpaEntered}
                                    onChange={(e) => setGpaEntered(e.target.value)}
                                    required
                                />
                            </div>

                            <div className={styles.modalFormGroup}>
                                <label className={styles.modalLabel}>Official Grade Sheet (PDF Only)</label>
                                <input
                                    type="file"
                                    className={styles.modalFile}
                                    onChange={handleFileChange}
                                    accept=".pdf"
                                    required
                                />
                            </div>

                            <div className={styles.modalActions}>
                                <button 
                                    type="button" 
                                    onClick={() => setShowApplyModal(false)} 
                                    className={styles.cancelBtn}
                                    disabled={submitting}
                                >
                                    Cancel
                                </button>
                                <button 
                                    type="submit" 
                                    className={styles.submitBtn} 
                                    disabled={submitting}
                                >
                                    {submitting ? 'Submitting...' : 'Submit Application'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default StudentDashboard;
