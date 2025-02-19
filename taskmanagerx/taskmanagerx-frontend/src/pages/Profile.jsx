import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import Header from '../components/Header';

const Profile = () => {
    const { user, fetchUserProfile } = useAuth();
    const navigate = useNavigate();

    const [name, setName] = useState(user?.name || '');
    const [success, setSuccess] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!user) {
            navigate('/login');
        } else {
            setName(user.name);
            setLoading(false);
        }
    }, [user, navigate]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            const response = await api.put('/users/update', { name });

            if (response.status === 200) {
                setSuccess('Profile updated successfully!');
                fetchUserProfile();
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to update profile');
        }
    };

    if (loading) {
        return <div className="text-center text-gray-500">Loading...</div>;
    }

    return (
        <div className="min-h-screen flex flex-col">
            <Header />
            <div className="flex-grow flex items-center justify-center px-4">
                <div className="flex flex-col items-center w-full max-w-md">
                    <h2 className="text-3xl font-bold text-gray-700 mb-6 text-center">Profile</h2>
                    <div className="bg-white shadow-md rounded px-8 pt-6 pb-8 w-96">
                        <div className="h-2 mb-6 text-center">
                            {error && <div className="text-red-500 text-sm font-bold">{error}</div>}
                            {success && <div className="text-green-500 text-sm font-bold">{success}</div>}
                        </div>

                        <div className="mb-4 text-center">
                            <div className="w-24 h-24 flex items-center justify-center rounded-full bg-gray-300 mx-auto">
                                {user?.profileImage ? (
                                    <img
                                        src={user.profileImage}
                                        alt="Profile"
                                        className="w-full h-full rounded-full object-cover"
                                    />
                                ) : (
                                    <span className="text-5xl font-bold text-black">
                                        {user?.name?.charAt(0).toUpperCase()}
                                    </span>
                                )}
                            </div>
                        </div>

                        <form onSubmit={handleSubmit}>
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2">Email</label>
                                <input
                                    className="shadow appearance-none border rounded w-full py-2 px-3 bg-gray-200 cursor-not-allowed"
                                    type="email"
                                    value={user.email}
                                    disabled
                                />
                            </div>

                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2">Name</label>
                                <input
                                    className="shadow appearance-none border rounded w-full py-2 px-3"
                                    type="text"
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    required
                                />
                            </div>

                            <button
                                type="submit"
                                className="bg-gray-700 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded w-full"
                            >
                                Save Changes
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Profile;
