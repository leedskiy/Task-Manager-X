import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext.jsx';
import Dashboard from './pages/Dashboard';
import Register from './pages/Register';
// import Login from './pages/Login';

const App = () => {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/register" element={<Register />} />
          {/* <Route path="/login" element={<Login />} /> */}
        </Routes>
      </Router>
    </AuthProvider>
  );
};

export default App;