import { Link } from 'react-router-dom';
import { useState, useEffect } from 'react';
import logo from '../assets/logo.png';
import api from '../api/axios';

function Header() {
    const [user, setUser] = useState(null);
    const [showMenu, setShowMenu] = useState(false);

    useEffect(() => {
        fetchUserProfile();
    }, []);

    const fetchUserProfile = async () => {
        try {
            const response = await api.get('/auth/me');
            const data = response.data;
            setUser({
                name: data.name,
                profileImage: data.profileImage || null,
            });
        } catch (error) {
            console.error('Error fetching profile:', error.response?.data?.message || error.message);
            localStorage.removeItem('token');
            setUser(null);
        }
    };

    const handleSignOut = async () => {
        try {
            await api.post('/auth/logout');
            localStorage.removeItem('token'); // clear token
            setUser(null);                    // reset state
            setShowMenu(false);               // close menu
        } catch (error) {
            console.error('Failed to log out:', error.response?.data?.message || error.message);
        }
    };

    return (
        <header className="flex justify-between items-center bg-white py-3 px-8 shadow-md">
            <div className="flex items-center gap-2">
                <Link to="/">
                    <img src={logo} alt="Logo" className="h-16" />
                </Link>
            </div>

            <div className="flex items-center gap-4 relative">
                {!user ? (
                    <>
                        <Link to="/register" className="px-4 py-2 bg-gray-700 text-white rounded hover:bg-gray-900 transition">
                            Sign up
                        </Link>
                        <Link to="/login" className="px-4 py-2 bg-gray-700 text-white rounded hover:bg-gray-900 transition">
                            Sign in
                        </Link>
                    </>
                ) : (
                    <>
                        <div className="relative">
                            <div
                                className="w-10 h-10 flex items-center justify-center rounded-full bg-gray-300 cursor-pointer"
                                onClick={() => setShowMenu(!showMenu)}
                            >
                                {user.profileImage ? (
                                    <img
                                        src={user.profileImage}
                                        alt="Profile"
                                        className="w-full h-full rounded-full object-cover"
                                    />
                                ) : (
                                    <span className="text-lg font-bold text-black">
                                        {user.name.charAt(0).toUpperCase()}
                                    </span>
                                )}
                            </div>

                            {showMenu && (
                                <div className="absolute right-0 mt-2 w-48 bg-white border rounded-lg shadow-lg">
                                    <div className="px-4 py-2 border-b text-black font-bold">
                                        {user.name}
                                    </div>
                                    <button
                                        onClick={handleSignOut}
                                        className="w-full text-left px-4 py-2 text-red-500 hover:bg-red-100 transition"
                                    >
                                        Sign out
                                    </button>
                                </div>
                            )}
                        </div>
                    </>
                )}
            </div>
        </header>
    );
}

export default Header;