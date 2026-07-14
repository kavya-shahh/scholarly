import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import styles from './FacultyDashboard.module.css';

export default function FacultyDashboard() {
    const { user, logout } = useAuth();
    const [applications, setApplications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState('PENDING_VERIFICATION'); // PENDING_VERIFICATION, APPROVED, REJECTED, ALL
    const [selectedApp, setSelectedApp] = useState(null);
    const [remarks, setRemarks] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [modalError, setModalError] = useState('');
    const [modalSuccess, setModalSuccess] = useState('');

    useEffect(() => {
        fetchApplications();
    }, []);

    const fetchApplications = async () => {
        setLoading(true);
        try {
            const response = await fetch('/api/v1/applications', {
                credentials: 'include'
            });
            if (response.ok) {
                const data = await response.json();
                setApplications(data);
            }
        } catch (err) {
            console.error('Failed to load applications:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleAuditClick = (app) => {
        setSelectedApp(app);
        setRemarks(app.facultyComments || '');
        setModalError('');
        setModalSuccess('');
    };

    const handleVerifySubmit = async (status) => {
        if (status === 'REJECTED' && !remarks.trim()) {
            setModalError('Please provide comments stating the reason for rejection.');
            return;
        }

        setSubmitting(true);
        setModalError('');
        setModalSuccess('');

        try {
            const response = await fetch(`/api/v1/applications/${selectedApp.id}/verify`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status, remarks }),
                credentials: 'include'
            });

            if (response.ok) {
                const updatedApp = await response.json();
                setModalSuccess(`Application has been successfully ${status.toLowerCase()}!`);
                // Update applications list in state
                setApplications(prev => prev.map(a => a.id === updatedApp.id ? updatedApp : a));
                setTimeout(() => {
                    setSelectedApp(null);
                }, 1500);
            } else {
                const errData = await response.json();
                setModalError(errData.message || 'Failed to update verification status.');
            }
        } catch (err) {
            setModalError('Connection error. Please try again.');
        } finally {
            setSubmitting(false);
        }
    };

    // Filter applications based on active tab
    const getFilteredApps = () => {
        if (activeTab === 'ALL') return applications;
        return applications.filter(app => app.status === activeTab);
    };

    const countByStatus = (status) => {
        return applications.filter(app => app.status === status).length;
    };

    const isMismatch = (app) => {
        return app.status === 'PENDING_VERIFICATION' &&
            app.facultyComments &&
            app.facultyComments.toLowerCase().includes('mismatch');
    };

    const isUnreadable = (app) => {
        return app.status === 'PENDING_VERIFICATION' &&
            app.facultyComments &&
            app.facultyComments.toLowerCase().includes('could not parse');
    };

    const formatStatusText = (status) => {
        switch (status) {
            case 'SUBMITTED': return 'Auto-Submitted';
            case 'PENDING_VERIFICATION': return 'Requires Audit';
            case 'APPROVED': return 'Approved';
            case 'REJECTED': return 'Rejected';
            default: return status;
        }
    };

    const getStatusStyle = (status) => {
        switch (status) {
            case 'APPROVED': return styles.statusApproved;
            case 'REJECTED': return styles.statusRejected;
            case 'PENDING_VERIFICATION': return styles.statusPending;
            default: return styles.statusSubmitted;
        }
    };

    return (
        <div className={styles.dashboardContainer}>
            <header className={styles.header}>
                <div className={styles.headerBrand}>
                    <span className={styles.logoBadge} style={{ backgroundColor: '#000000ec' }}>S</span>
                    <h1 className={styles.title} style={{ color: '#d33499ff' }}>scholarly <span className={styles.roleLabel} style={{ color: '#b9b910ff' }}>Faculty Audit</span></h1>

                </div>
                <div className={styles.profileSection}>
                    <div className={styles.profileInfo}>
                        <span className={styles.profileName}>Prof. {user?.firstName} {user?.lastName}</span>
                        <span className={styles.profileDetails}>{user?.email}</span>
                    </div>
                    <button onClick={logout} className={styles.logoutBtn}>Sign Out</button>
                </div>
            </header>

            <main className={styles.mainContent}>
                {/* Metric Indicators Row */}
                <div className={styles.metricsGrid}>
                    <div className={styles.metricCard}>
                        <div className={styles.metricTitle}>Pending Audits</div>
                        <div className={`${styles.metricVal} ${styles.textPending}`}>{countByStatus('PENDING_VERIFICATION')}</div>
                    </div>
                    <div className={styles.metricCard}>
                        <div className={styles.metricTitle}>Approved Applications</div>
                        <div className={`${styles.metricVal} ${styles.textApproved}`}>{countByStatus('APPROVED')}</div>
                    </div>
                    <div className={styles.metricCard}>
                        <div className={styles.metricTitle}>Rejected Applications</div>
                        <div className={`${styles.metricVal} ${styles.textRejected}`}>{countByStatus('REJECTED')}</div>
                    </div>
                </div>

                {/* Tabs Panel */}
                <div className={styles.tabsSection}>
                    <button
                        className={`${styles.tabBtn} ${activeTab === 'PENDING_VERIFICATION' ? styles.activeTab : ''}`}
                        onClick={() => setActiveTab('PENDING_VERIFICATION')}
                    >
                        Pending Audit ({countByStatus('PENDING_VERIFICATION')})
                    </button>
                    <button
                        className={`${styles.tabBtn} ${activeTab === 'APPROVED' ? styles.activeTab : ''}`}
                        onClick={() => setActiveTab('APPROVED')}
                    >
                        Approved ({countByStatus('APPROVED')})
                    </button>
                    <button
                        className={`${styles.tabBtn} ${activeTab === 'REJECTED' ? styles.activeTab : ''}`}
                        onClick={() => setActiveTab('REJECTED')}
                    >
                        Rejected ({countByStatus('REJECTED')})
                    </button>
                    <button
                        className={`${styles.tabBtn} ${activeTab === 'ALL' ? styles.activeTab : ''}`}
                        onClick={() => setActiveTab('ALL')}
                    >
                        All Applications ({applications.length})
                    </button>
                </div>

                {loading ? (
                    <div className={styles.loaderContainer}>
                        <div className={styles.spinner}></div>
                    </div>
                ) : (
                    <div className={styles.tableWrapper}>
                        {getFilteredApps().length === 0 ? (
                            <div className={styles.emptyState}>No applications found in this queue.</div>
                        ) : (
                            <table className={styles.table}>
                                <thead>
                                    <tr>
                                        <th>Student Details</th>
                                        <th>Scholarship Name</th>
                                        <th>Gpa Declared</th>
                                        <th>OCR Extracted</th>
                                        <th>System Flag Remarks</th>
                                        <th>Applied On</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {getFilteredApps().map(app => (
                                        <tr key={app.id} className={styles.row}>
                                            <td>
                                                <div className={styles.studentName}>{app.studentName}</div>
                                                <div className={styles.studentEmail}>{app.studentEmail}</div>
                                                <div className={styles.studentMeta}>Dept: {app.studentDepartment} | Roll: {app.studentEnrollment}</div>
                                            </td>
                                            <td>
                                                <div className={styles.scholarshipTitle}>{app.scholarshipTitle}</div>
                                                <div className={styles.scholarshipAmount}>₹{app.scholarshipAmount.toLocaleString('en-IN', { maximumFractionDigits: 0 })}</div>
                                            </td>
                                            <td><span className={styles.gpaText}>{app.gpaEntered}</span></td>
                                            <td>
                                                <span className={styles.gpaText}>{app.gpaExtracted || 'N/A'}</span>
                                            </td>
                                            <td>
                                                {isMismatch(app) && (
                                                    <span className={`${styles.flagBadge} ${styles.mismatchBadge}`}>GPA Mismatch</span>
                                                )}
                                                {isUnreadable(app) && (
                                                    <span className={`${styles.flagBadge} ${styles.unreadableBadge}`}>Unreadable Document</span>
                                                )}
                                                {!isMismatch(app) && !isUnreadable(app) && app.facultyComments && (
                                                    <span className={styles.systemText}>{app.facultyComments}</span>
                                                )}
                                                {!app.facultyComments && <span className={styles.mutedText}>None</span>}
                                            </td>
                                            <td>{new Date(app.appliedAt).toLocaleDateString('en-GB')}</td>
                                            <td>
                                                {app.status === 'PENDING_VERIFICATION' ? (
                                                    <button
                                                        onClick={() => handleAuditClick(app)}
                                                        className={styles.auditBtn}
                                                    >
                                                        Audit File
                                                    </button>
                                                ) : (
                                                    <span className={`${styles.statusText} ${getStatusStyle(app.status)}`}>
                                                        {formatStatusText(app.status)}
                                                    </span>
                                                )}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        )}
                    </div>
                )}
            </main>

            {/* Audit Modal overlay */}
            {selectedApp && (
                <div className={styles.modalOverlay}>
                    <div className={styles.modalContent}>
                        <div className={styles.modalHeader}>
                            <div>
                                <h2 className={styles.modalTitle}>Audit Application for {selectedApp.studentName}</h2>
                                <p className={styles.modalSubtitle}>{selectedApp.scholarshipTitle} — ₹{selectedApp.scholarshipAmount.toLocaleString('en-IN', { maximumFractionDigits: 0 })}</p>
                            </div>
                            <button onClick={() => setSelectedApp(null)} className={styles.closeBtn}>&times;</button>
                        </div>

                        {modalError && <div className={styles.modalError}>{modalError}</div>}
                        {modalSuccess && <div className={styles.modalSuccess}>{modalSuccess}</div>}

                        <div className={styles.modalBody}>
                            {/* Left Side details and forms */}
                            <div className={styles.auditFormArea}>
                                <div className={styles.metaBox}>
                                    <h3 className={styles.metaTitle}>Applicant Verification Profile</h3>
                                    <div className={styles.metaGrid}>
                                        <div><strong>Student Declared GPA:</strong> {selectedApp.gpaEntered}</div>
                                        <div><strong>OCR Extracted GPA:</strong> {selectedApp.gpaExtracted || 'Could not parse'}</div>
                                        <div><strong>Profile Base GPA:</strong> {selectedApp.studentProfileGpa}</div>
                                        <div><strong>Dept Authority:</strong> {selectedApp.studentDepartment}</div>
                                        <div><strong>Enrollment Number:</strong> {selectedApp.studentEnrollment}</div>
                                    </div>
                                    {selectedApp.facultyComments && (
                                        <div className={styles.systemAlert}>
                                            <strong>Audit Alert Remarks:</strong>
                                            <p>{selectedApp.facultyComments}</p>
                                        </div>
                                    )}
                                </div>

                                <div className={styles.remarksFormGroup}>
                                    <label className={styles.remarksLabel}>Faculty Review Remarks (Visible to student)</label>
                                    <textarea
                                        rows="4"
                                        className={styles.remarksTextarea}
                                        placeholder="Add verification notes, match approvals, or rejection reasons here..."
                                        value={remarks}
                                        onChange={(e) => setRemarks(e.target.value)}
                                    />
                                </div>

                                <div className={styles.modalActions}>
                                    <button
                                        onClick={() => handleVerifySubmit('APPROVED')}
                                        disabled={submitting}
                                        className={styles.approveBtn}
                                    >
                                        Approve & Verify
                                    </button>
                                    <button
                                        onClick={() => handleVerifySubmit('REJECTED')}
                                        disabled={submitting}
                                        className={styles.rejectBtn}
                                    >
                                        Reject Submission
                                    </button>
                                </div>
                            </div>

                            {/* Right Side Transcript View */}
                            <div className={styles.transcriptPreview}>
                                <div className={styles.previewHeader}>
                                    <span>Uploaded Grade Sheet Preview</span>
                                    <a
                                        href={`/api${selectedApp.transcriptUrl}`}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                        className={styles.openExternalBtn}
                                    >
                                        Open Full PDF
                                    </a>
                                </div>
                                <iframe
                                    src={`/api${selectedApp.transcriptUrl}`}
                                    className={styles.previewIframe}
                                    title="Grade Sheet Transcript Preview"
                                />
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
