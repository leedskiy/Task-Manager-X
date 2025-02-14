import Header from '../components/Header'
import { Link } from 'react-router-dom'

function Dashboard() {
    return (
        <div>
            <Header />
            <div className="p-8 text-center">
                <h1 className="text-3xl font-bold mb-6">Dashboard</h1>
                <Link to="/add-task" className="px-6 py-3 bg-gray-700 text-white rounded hover:bg-gray-900 transition">
                    Add a Task
                </Link>
            </div>
        </div>
    )
}

export default Dashboard;