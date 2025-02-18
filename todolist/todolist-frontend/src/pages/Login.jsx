import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { FaGoogle } from "react-icons/fa";

const Login = () => {
    const navigate = useNavigate();
    const { login, fetchUserProfile } = useAuth();

    const [email, setEmail] = useState(sessionStorage.getItem('loginEmail') || '');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(sessionStorage.getItem('loginError') || '');

    useEffect(() => {
        const handleBeforeUnload = () => {
            sessionStorage.removeItem('loginEmail');
            sessionStorage.removeItem('loginError');
        };

        window.addEventListener('beforeunload', handleBeforeUnload);

        return () => {
            window.removeEventListener('beforeunload', handleBeforeUnload);
        };
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        sessionStorage.removeItem('loginError');

        try {
            await login({ email, password });
            await fetchUserProfile();
            sessionStorage.clear();
            navigate('/');
        } catch (err) {
            const errorMessage = err.response?.data?.message || 'Invalid email or password';
            setError(errorMessage);
            sessionStorage.setItem('loginEmail', email);
            sessionStorage.setItem('loginError', errorMessage);
        }
    };

    const handleGoogleAuth = () => {
        window.location.href = 'http://localhost:8080/oauth2/login/google';
    };

    useEffect(() => {
        sessionStorage.removeItem('loginEmail');
        sessionStorage.removeItem('loginError');
    }, [navigate]);

    return (
        <div className="min-h-screen flex flex-col justify-center items-center">
            <h2 className="text-3xl font-bold text-gray-700 mb-6">Login</h2>
            <form
                onSubmit={handleSubmit}
                className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-96"
            >
                <div className="h-2 mb-4 text-center">
                    {error && (
                        <div className="text-red-500 text-sm font-bold">
                            {error}
                        </div>
                    )}
                </div>

                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">Email</label>
                    <input
                        className="shadow appearance-none border rounded w-full py-2 px-3"
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">Password</label>
                    <input
                        className="shadow appearance-none border rounded w-full py-2 px-3"
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>

                <button
                    type="submit"
                    className="bg-gray-700 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded w-full mb-4"
                >
                    Login
                </button>

                <button
                    type="button"
                    onClick={handleGoogleAuth}
                    className="flex items-center justify-center gap-2 bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded w-full"
                >
                    Continue with Google
                    <FaGoogle size={20} />
                </button>
            </form>

            <p className="mt-4 font-bold text-gray-700">
                Don't have an account?{' '}
                <Link to="/register" className="text-blue-500">Register</Link>
            </p>
        </div>
    );
};

export default Login;