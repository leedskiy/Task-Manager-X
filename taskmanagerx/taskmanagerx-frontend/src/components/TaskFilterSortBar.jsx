import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../api/axios";

const TaskFilterSortBar = ({ setTasks }) => {
    const { user, isAdmin } = useAuth();

    const [status, setStatus] = useState("");
    const [dueDateBefore, setDueDateBefore] = useState("");
    const [dueDateAfter, setDueDateAfter] = useState("");
    const [sortBy, setSortBy] = useState("dueDate");
    const [sortOrder, setSortOrder] = useState("asc");
    const [userEmail, setUserEmail] = useState("");
    const [users, setUsers] = useState([]);

    useEffect(() => {
        if (isAdmin) {
            api.get("/admin/users")
                .then((response) => setUsers(response.data))
                .catch((error) => console.error("Failed to fetch users:", error));
        }
    }, [isAdmin]);

    const formatDateTime = (date) => {
        return date ? `${date}T00:00:00` : null;
    };

    const fetchFilteredTasks = async () => {
        try {
            const endpoint = isAdmin ? "/admin/tasks/filter" : "/tasks/filter";
            const response = await api.get(endpoint, {
                params: {
                    userEmail: isAdmin ? userEmail || null : null,
                    status: status || null,
                    dueDateBefore: formatDateTime(dueDateBefore),
                    dueDateAfter: formatDateTime(dueDateAfter),
                },
            });

            setTasks(response.data);
        } catch (error) {
            console.error("Error filtering tasks:", error);
        }
    };

    const fetchSortedTasks = async () => {
        try {
            const endpoint = isAdmin ? "/admin/tasks/sort" : "/tasks/sort";
            const response = await api.get(endpoint, {
                params: { sortBy: isAdmin ? sortBy : "dueDate", order: sortOrder },
            });

            setTasks(response.data);
        } catch (error) {
            console.error("Error sorting tasks:", error);
        }
    };

    return (
        <div className="bg-white shadow-md rounded-lg p-4 w-full max-w-3xl mx-auto mt-6">
            <div className="flex flex-wrap gap-4 justify-center mb-4">
                {isAdmin && (
                    <div className="flex flex-col w-40">
                        <label className="text-gray-600 text-sm font-semibold mb-1">Sort By</label>
                        <select
                            className="border rounded p-2"
                            value={sortBy}
                            onChange={(e) => setSortBy(e.target.value)}
                        >
                            <option value="dueDate">Due Date</option>
                            <option value="userEmail">User Email</option>
                        </select>
                    </div>
                )}

                <div className="flex flex-col w-40">
                    <label className="text-gray-600 text-sm font-semibold mb-1">Sort Order {!isAdmin ? "(due date)" : ""}</label>
                    <select
                        className="border rounded p-2"
                        value={sortOrder}
                        onChange={(e) => setSortOrder(e.target.value)}
                    >
                        <option value="asc">Ascending</option>
                        <option value="desc">Descending</option>
                    </select>
                </div>
            </div>

            <div className="flex justify-center mb-4">
                <button
                    className="px-4 py-2 bg-gray-700 text-white font-semibold rounded-lg hover:bg-gray-900 transition"
                    onClick={fetchSortedTasks}
                >
                    Sort Tasks
                </button>
            </div>

            <div className="flex flex-wrap gap-4 justify-center mb-4">
                <div className="flex flex-col w-40">
                    <label className="text-gray-600 text-sm font-semibold mb-1">Due After</label>
                    <input
                        type="date"
                        className="border rounded p-2"
                        value={dueDateAfter}
                        onChange={(e) => setDueDateAfter(e.target.value)}
                    />
                </div>

                <div className="flex flex-col w-40">
                    <label className="text-gray-600 text-sm font-semibold mb-1">Due Before</label>
                    <input
                        type="date"
                        className="border rounded p-2"
                        value={dueDateBefore}
                        onChange={(e) => setDueDateBefore(e.target.value)}
                    />
                </div>

                {!isAdmin && (
                    <div className="flex flex-col w-40">
                        <label className="text-gray-600 text-sm font-semibold mb-1">Status</label>
                        <select
                            className="border rounded p-2"
                            value={status}
                            onChange={(e) => setStatus(e.target.value)}
                        >
                            <option value="">All</option>
                            <option value="PENDING">Pending</option>
                            <option value="COMPLETED">Completed</option>
                        </select>
                    </div>
                )}
            </div>

            {isAdmin && (
                <div className="flex flex-wrap gap-4 justify-center mb-4">
                    <div className="flex flex-col w-40">
                        <label className="text-gray-600 text-sm font-semibold mb-1">Status</label>
                        <select
                            className="border rounded p-2"
                            value={status}
                            onChange={(e) => setStatus(e.target.value)}
                        >
                            <option value="">All</option>
                            <option value="PENDING">Pending</option>
                            <option value="COMPLETED">Completed</option>
                        </select>
                    </div>


                    <div className="flex flex-col w-40">
                        <label className="text-gray-600 text-sm font-semibold mb-1">User Email</label>
                        <select
                            className="border rounded p-2"
                            value={userEmail}
                            onChange={(e) => setUserEmail(e.target.value)}
                        >
                            <option value="">All Users</option>
                            {users.map((user) => (
                                <option key={user.id} value={user.email}>
                                    {user.email}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>
            )}

            <div className="flex justify-center">
                <button
                    className="px-4 py-2 bg-gray-700 text-white font-semibold rounded-lg hover:bg-gray-900 transition"
                    onClick={fetchFilteredTasks}
                >
                    Apply Filters
                </button>
            </div>
        </div>
    );
};

export default TaskFilterSortBar;
