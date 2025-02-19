import { useState } from "react";
import api from "../api/axios";

const TaskFilterSortBar = ({ setTasks }) => {
    const [status, setStatus] = useState("");
    const [dueDateBefore, setDueDateBefore] = useState("");
    const [dueDateAfter, setDueDateAfter] = useState("");
    const [sortOrder, setSortOrder] = useState("asc");

    const formatDateTime = (date) => {
        return date ? `${date}T00:00:00` : null;
    };

    const fetchFilteredTasks = async () => {
        try {
            const response = await api.get("/tasks/filter", {
                params: {
                    status: status || null,
                    dueDateBefore: formatDateTime(dueDateBefore),
                    dueDateAfter: formatDateTime(dueDateAfter),
                },
            });

            let filteredTasks = response.data;

            if (sortOrder) {
                filteredTasks = sortTasks(filteredTasks, sortOrder);
            }

            setTasks(filteredTasks);
        } catch (error) {
            console.error("Error filtering tasks:", error);
        }
    };

    const fetchSortedTasks = async () => {
        try {
            const response = await api.get("/tasks/sort", {
                params: { order: sortOrder },
            });

            let sortedTasks = response.data;

            if (status || dueDateBefore || dueDateAfter) {
                sortedTasks = sortedTasks.filter((task) => {
                    return (
                        (!status || task.status === status) &&
                        (!dueDateBefore || new Date(task.dueDate) < new Date(dueDateBefore)) &&
                        (!dueDateAfter || new Date(task.dueDate) > new Date(dueDateAfter))
                    );
                });
            }

            setTasks(sortedTasks);
        } catch (error) {
            console.error("Error sorting tasks:", error);
        }
    };

    const sortTasks = (tasks, order) => {
        return tasks.sort((a, b) => {
            const dateA = new Date(a.dueDate);
            const dateB = new Date(b.dueDate);
            return order === "asc" ? dateA - dateB : dateB - dateA;
        });
    };

    return (
        <div className="bg-white shadow-md rounded-lg p-4 w-full max-w-3xl mx-auto mt-6">
            <div className="flex flex-wrap gap-4 justify-center mb-4">
                <div className="flex flex-col w-40">
                    <label className="text-gray-600 text-sm font-semibold mb-1">Sort By</label>
                    <select
                        className="border rounded p-2"
                        value={sortOrder}
                        onChange={(e) => setSortOrder(e.target.value)}
                    >
                        <option value="asc">Due Date (Ascending)</option>
                        <option value="desc">Due Date (Descending)</option>
                    </select>
                </div>

                <button
                    className="px-4 py-2 bg-gray-700 text-white font-semibold rounded-lg hover:bg-gray-900 transition self-end"
                    onClick={fetchSortedTasks}
                >
                    Sort Tasks
                </button>
            </div>

            <div className="flex flex-wrap gap-4 justify-center">
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

                <button
                    className="px-4 py-2 bg-gray-700 text-white font-semibold rounded-lg hover:bg-gray-900 transition self-end"
                    onClick={fetchFilteredTasks}
                >
                    Apply Filters
                </button>
            </div>
        </div>
    );
};

export default TaskFilterSortBar;
