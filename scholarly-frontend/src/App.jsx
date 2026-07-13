import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './features/auth/Login';
import Register from './features/auth/Register';

// Basic Dashboard Placeholders for authentication testing
const StudentDashboard = () => {
  const { user, logout } = useAuth();
  return (
    <div style={dashboardStyles.container}>
      <div style={dashboardStyles.card}>
        <h1 style={dashboardStyles.roleBadge}>Student Portal</h1>
        <h2 style={dashboardStyles.greeting}>Welcome, {user?.firstName} {user?.lastName}!</h2>
        <div style={dashboardStyles.infoGrid}>
          <div style={dashboardStyles.infoItem}>
            <strong>Email:</strong> {user?.email}
          </div>
          <div style={dashboardStyles.infoItem}>
            <strong>Enrollment No:</strong> {user?.enrollmentNumber}
          </div>
          <div style={dashboardStyles.infoItem}>
            <strong>Department:</strong> {user?.department}
          </div>
          <div style={dashboardStyles.infoItem}>
            <strong>Current GPA:</strong> {user?.gpa} / 10.00
          </div>
        </div>
        <button onClick={logout} style={dashboardStyles.logoutBtn}>Sign Out</button>
      </div>
    </div>
  );
};

const FacultyDashboard = () => {
  const { user, logout } = useAuth();
  return (
    <div style={dashboardStyles.container}>
      <div style={dashboardStyles.card}>
        <h1 style={{...dashboardStyles.roleBadge, color: '#38bdf8', borderColor: '#38bdf8'}}>Faculty Portal</h1>
        <h2 style={dashboardStyles.greeting}>Welcome, Prof. {user?.firstName} {user?.lastName}!</h2>
        <div style={dashboardStyles.infoGrid}>
          <div style={dashboardStyles.infoItem}>
            <strong>Email:</strong> {user?.email}
          </div>
          <div style={dashboardStyles.infoItem}>
            <strong>Department Authority:</strong> Verification Panel
          </div>
        </div>
        <button onClick={logout} style={dashboardStyles.logoutBtn}>Sign Out</button>
      </div>
    </div>
  );
};

const AdminDashboard = () => {
  const { user, logout } = useAuth();
  return (
    <div style={dashboardStyles.container}>
      <div style={dashboardStyles.card}>
        <h1 style={{...dashboardStyles.roleBadge, color: '#a855f7', borderColor: '#a855f7'}}>Admin Portal</h1>
        <h2 style={dashboardStyles.greeting}>Welcome Director, {user?.firstName} {user?.lastName}!</h2>
        <div style={dashboardStyles.infoGrid}>
          <div style={dashboardStyles.infoItem}>
            <strong>Email:</strong> {user?.email}
          </div>
          <div style={dashboardStyles.infoItem}>
            <strong>System Permissions:</strong> ROOT / ALL
          </div>
        </div>
        <button onClick={logout} style={dashboardStyles.logoutBtn}>Sign Out</button>
      </div>
    </div>
  );
};

const Unauthorized = () => {
  const { user } = useAuth();
  const getHomeRedirect = () => {
    if (!user) return '/login';
    if (user.role === 'STUDENT') return '/student/dashboard';
    if (user.role === 'FACULTY') return '/faculty/dashboard';
    if (user.role === 'ADMIN') return '/admin/dashboard';
    return '/login';
  };

  return (
    <div style={dashboardStyles.container}>
      <div style={{...dashboardStyles.card, textAlign: 'center'}}>
        <h1 style={{color: '#f75a5a', fontSize: '48px', marginBottom: '10px'}}>Access Denied</h1>
        <p style={{color: '#8d8d99', marginBottom: '24px'}}>You do not have administrative clearance to access this portal.</p>
        <Link to={getHomeRedirect()} style={dashboardStyles.backLink}>Back to My Portal</Link>
      </div>
    </div>
  );
};

// Root Redirect handler depending on active session state
const HomeRedirect = () => {
  const { user, loading } = useAuth();

  if (loading) {
    return <div style={{color: '#fff', textAlign: 'center', marginTop: '20%'}}>Loading...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (user.role === 'STUDENT') return <Navigate to="/student/dashboard" replace />;
  if (user.role === 'FACULTY') return <Navigate to="/faculty/dashboard" replace />;
  if (user.role === 'ADMIN') return <Navigate to="/admin/dashboard" replace />;

  return <Navigate to="/login" replace />;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Public Authentication Views */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/unauthorized" element={<Unauthorized />} />

          {/* Protected Portal Views */}
          <Route 
            path="/student/dashboard" 
            element={
              <ProtectedRoute allowedRoles={['STUDENT']}>
                <StudentDashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/faculty/dashboard" 
            element={
              <ProtectedRoute allowedRoles={['FACULTY']}>
                <FacultyDashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/admin/dashboard" 
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminDashboard />
              </ProtectedRoute>
            } 
          />

          {/* Fallback routing */}
          <Route path="/" element={<HomeRedirect />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

const dashboardStyles = {
  container: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: '100vh',
    background: '#121214',
    color: '#e1e1e6',
    fontFamily: 'system-ui, -apple-system, sans-serif',
    padding: '20px'
  },
  card: {
    width: '100%',
    maxWidth: '600px',
    backgroundColor: '#202024',
    border: '1px solid #29292e',
    borderRadius: '12px',
    padding: '40px',
    boxShadow: '0 4px 16px rgba(0,0,0,0.4)',
    position: 'relative'
  },
  roleBadge: {
    display: 'inline-block',
    fontSize: '12px',
    fontWeight: '700',
    color: '#00b37e',
    border: '1px solid #00b37e',
    borderRadius: '20px',
    padding: '4px 12px',
    textTransform: 'uppercase',
    letterSpacing: '1px',
    marginBottom: '16px'
  },
  greeting: {
    fontSize: '26px',
    fontWeight: '700',
    marginBottom: '24px'
  },
  infoGrid: {
    display: 'grid',
    gridTemplateColumns: '1fr',
    gap: '12px',
    backgroundColor: '#121214',
    padding: '20px',
    borderRadius: '8px',
    marginBottom: '30px'
  },
  infoItem: {
    fontSize: '15px',
    color: '#c4c4cc'
  },
  logoutBtn: {
    background: '#f75a5a',
    color: '#fff',
    border: 'none',
    borderRadius: '6px',
    padding: '12px 24px',
    fontSize: '14px',
    fontWeight: '600',
    cursor: 'pointer',
    transition: 'opacity 0.2s',
  },
  backLink: {
    color: '#00b37e',
    textDecoration: 'none',
    fontWeight: '600',
  }
};

export default App;
