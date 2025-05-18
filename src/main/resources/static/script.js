
const App = () => {
    const [spellChecks, setSpellChecks] = React.useState([]);
    const [newSpellCheck, setNewSpellCheck] = React.useState({ name: "", texts: "" });
    const [editSpellCheck, setEditSpellCheck] = React.useState(null);
    const [isLoading, setIsLoading] = React.useState(false);
    const [theme, setTheme] = React.useState("light");

    const dictionary = ["hello", "world", "test", "code", "react", "check", "word"];

    const checkSpelling = (text) => {
        const words = text
            .split(",")
            .map((word) => word.trim().toLowerCase())
            .filter((word) => word);
        const errors = words.filter((word) => !dictionary.includes(word));
        return errors.length > 0 ? `Errors: ${errors.join(", ")}` : "No errors";
    };

    const toggleTheme = () => {
        const newTheme = theme === "light" ? "dark" : "light";
        setTheme(newTheme);
        document.body.className = newTheme + "-theme";
        localStorage.setItem("theme", newTheme);
    };

    React.useEffect(() => {
        setSpellChecks([
            {
                id: 1,
                name: "SpellCheck_1695234567890",
                texts: "Hello, world",
                status: "OK",
                error: "No errors",
                categories: [{ name: "Category1" }],
            },
            {
                id: 2,
                name: "SpellCheck_1695234567891",
                texts: "Test, misttake",
                status: "Error",
                error: "Errors: misttake",
                categories: [{ name: "Category2" }],
            },
        ]);

        const savedTheme = localStorage.getItem("theme") || "light";
        setTheme(savedTheme);
        document.body.className = savedTheme + "-theme";
    }, []);

    const handleCreate = (e) => {
        e.preventDefault();
        if (!newSpellCheck.texts.trim()) {
            Toastify({
                text: "Please enter texts",
                duration: 3000,
                className: "bg-yellow-500 text-white",
            }).showToast();
            return;
        }
        setIsLoading(true);
        const timestamp = Date.now();
        const newId = spellChecks.length ? Math.max(...spellChecks.map(sc => sc.id)) + 1 : 1;
        const spellingResult = checkSpelling(newSpellCheck.texts);
        const newEntry = {
            id: newId,
            name: `SpellCheck_${timestamp}`,
            texts: newSpellCheck.texts.trim(),
            status: spellingResult === "No errors" ? "OK" : "Error",
            error: spellingResult,
            categories: [{ name: "Default" }],
        };
        setSpellChecks(prev => [...prev, newEntry]);
        setNewSpellCheck({ name: "", texts: "" });
        setIsLoading(false);
        Toastify({
            text: `Added spell check: ${newSpellCheck.texts}`,
            duration: 3000,
            className: "bg-green-500 text-white",
        }).showToast();
    };

    const handleUpdate = (e) => {
        e.preventDefault();
        if (!editSpellCheck.texts.trim()) {
            Toastify({
                text: "Texts cannot be empty",
                duration: 3000,
                className: "bg-yellow-500 text-white",
            }).showToast();
            return;
        }
        setIsLoading(true);
        const spellingResult = checkSpelling(editSpellCheck.texts);
        const updatedEntry = {
            ...editSpellCheck,
            status: spellingResult === "No errors" ? "OK" : "Error",
            error: spellingResult,
        };
        setSpellChecks(spellChecks.map((sc) => (sc.id === editSpellCheck.id ? updatedEntry : sc)));
        setEditSpellCheck(null);
        setIsLoading(false);
        Toastify({
            text: "Spell check updated",
            duration: 3000,
            className: "bg-green-500 text-white",
        }).showToast();
    };

    const handleDelete = (id) => {
        setIsLoading(true);
        setSpellChecks(spellChecks.filter((sc) => sc.id !== id));
        setIsLoading(false);
        Toastify({
            text: "Spell check deleted",
            duration: 3000,
            className: "bg-green-500 text-white",
        }).showToast();
    };

    return (
        <div className="container">
            <header>
                <h1>Spell Check Categories</h1>
                <button className="theme-toggle" onClick={toggleTheme}>
                    <i className={theme === "light" ? "fas fa-moon" : "fas fa-sun"}></i>
                    {theme === "light" ? "Dark Theme" : "Light Theme"}
                </button>
            </header>

            <div>
                <h2>Add Spell Check</h2>
                <form onSubmit={handleCreate}>
                    <div>
                        <label>Name</label>
                        <input
                            type="text"
                            value={`SpellCheck_${Date.now()}`}
                            readOnly
                        />
                    </div>
                    <div>
                        <label>Texts (comma-separated)</label>
                        <input
                            type="text"
                            value={newSpellCheck.texts}
                            onChange={(e) =>
                                setNewSpellCheck({ ...newSpellCheck, texts: e.target.value })
                            }
                            placeholder="hello, world, test"
                        />
                    </div>
                    <button type="submit" disabled={isLoading}>
                        {isLoading ? "Adding..." : "Add Spell Check"}
                    </button>
                </form>
            </div>

            {editSpellCheck && (
                <div>
                    <h2>Edit Spell Check</h2>
                    <form onSubmit={handleUpdate}>
                        <div>
                            <label>Name</label>
                            <input
                                type="text"
                                value={editSpellCheck.name}
                                readOnly
                            />
                        </div>
                        <div>
                            <label>Texts</label>
                            <input
                                type="text"
                                value={editSpellCheck.texts}
                                onChange={(e) =>
                                    setEditSpellCheck({ ...editSpellCheck, texts: e.target.value })
                                }
                                required
                            />
                        </div>
                        <div>
                            <label>Status</label>
                            <input
                                type="text"
                                value={editSpellCheck.status}
                                readOnly
                            />
                        </div>
                        <div>
                            <label>Error</label>
                            <input
                                type="text"
                                value={editSpellCheck.error || ""}
                                readOnly
                            />
                        </div>
                        <div>
                            <button type="submit" disabled={isLoading}>
                                {isLoading ? "Updating..." : "Update"}
                            </button>
                            <button
                                type="button"
                                onClick={() => setEditSpellCheck(null)}
                            >
                                Cancel
                            </button>
                        </div>
                    </form>
                </div>
            )}

            <div>
                {isLoading ? (
                    <div>Loading...</div>
                ) : spellChecks.length === 0 ? (
                    <div>No records to display</div>
                ) : (
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Status</th>
                            <th>Error</th>
                            <th>Categories</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {spellChecks.map((spellCheck) => (
                            <tr key={spellCheck.id}>
                                <td>{spellCheck.id}</td>
                                <td>{spellCheck.name}</td>
                                <td>{spellCheck.status}</td>
                                <td>{spellCheck.error || "-"}</td>
                                <td>
                                    {spellCheck.categories
                                        .map((cat) => cat.name)
                                        .join(", ") || "-"}
                                </td>
                                <td>
                                    <button
                                        className="edit"
                                        onClick={() => setEditSpellCheck(spellCheck)}
                                    >
                                        <i className="fas fa-edit"></i>
                                    </button>
                                    <button
                                        className="delete"
                                        onClick={() => handleDelete(spellCheck.id)}
                                    >
                                        <i className="fas fa-trash"></i>
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
};

try {
    ReactDOM.render(<App />, document.getElementById("root"));
} catch (error) {
    console.error("Failed to render React app:", error);
}