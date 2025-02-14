import { Link } from 'react-router-dom';
import logo from '../assets/logo.png';

function Header() {
    return (
        <header className="flex justify-between items-center bg-white text-white py-3 px-8 shadow-md">
            <div className="flex items-center gap-2">
                <Link to="/">
                    <img src={logo} alt="Logo" className="h-16" />
                </Link>
            </div>
            <div className="flex gap-4">
                <Link to="/register" className="px-4 py-2 bg-gray-700 rounded hover:bg-gray-900 transition">
                    Register
                </Link>
                <Link to="/login" className="px-4 py-2 bg-gray-700 rounded hover:bg-gray-900 transition">
                    Login
                </Link>
            </div>
        </header>
    );
}

export default Header;
