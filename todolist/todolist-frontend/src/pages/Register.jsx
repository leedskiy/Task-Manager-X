import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { FaGoogle } from "react-icons/fa";

const Register = () => {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const { login, fetchUserProfile } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const registerResponse = await api.post('/auth/register', {
                name,
                email,
                password,
            });

            if (registerResponse.status === 200) {
                await login({ email, password });

                await fetchUserProfile();
                navigate('/');
            } else {
                setError('Registration failed');
            }
        } catch (err) {
            setError(err.response?.data?.message || 'An error occurred');
        }
    };

    const handleGoogleAuth = () => {
        window.location.href = 'http://localhost:8080/oauth2/login/google';
    };

    return (
        <div className="min-h-screen flex flex-col justify-center items-center">
            <h2 className="text-3xl font-bold text-gray-700 mb-6">Register</h2>
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
                    <label className="block text-gray-700 text-sm font-bold mb-2">Name</label>
                    <input
                        className="shadow appearance-none border rounded w-full py-2 px-3"
                        type="text"
                        placeholder="Name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
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
                    Register
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
                Already have an account?{' '}
                <Link to="/login" className="text-blue-500">Login</Link>
            </p>
        </div>
    );
};

export default Register;