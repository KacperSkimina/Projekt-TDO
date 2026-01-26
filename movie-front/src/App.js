import React, { useState, useEffect } from 'react';
import axios from 'axios';

// To jest adres Twojego backendu na Railway
const API_URL = "https://projekt-tdo-production.up.railway.app/api";

function App() {
    const [movies, setMovies] = useState([]);
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [user, setUser] = useState({ username: '', password: '' });
    const [newMovie, setNewMovie] = useState({ title: '', releaseYear: 2024, description: '' });

    // 1. Pobieranie filmów
    const fetchMovies = async () => {
        try {
            const res = await axios.get(`${API_URL}/movies`);
            setMovies(res.data);
        } catch (err) {
            console.error("Błąd podczas pobierania filmów:", err);
        }
    };

    useEffect(() => {
        fetchMovies();
    }, []);

    // 2. Logowanie
    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const res = await axios.post(`${API_URL}/auth/login`, user);
            localStorage.setItem('token', res.data.token);
            setToken(res.data.token);
            alert("Zalogowano pomyślnie!");
        } catch (err) {
            alert("Błąd logowania! Sprawdź dane.");
        }
    };

    // 3. Dodawanie filmu 
    const handleAddMovie = async (e) => {
        e.preventDefault();
        try {
            await axios.post(`${API_URL}/movies`, newMovie, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            
            setNewMovie({ title: '', releaseYear: 2024, description: '' });
            fetchMovies();
            alert("Film dodany do bazy!");
        } catch (err) {
            alert("Błąd: Twoja sesja wygasła lub nie masz uprawnień (403).");
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
        setToken(null);
    };

    return (
        <div style={{ padding: '40px', fontFamily: 'Segoe UI, sans-serif', maxWidth: '800px', margin: '0 auto' }}>
            <h1 style={{ color: '#2c3e50', textAlign: 'center' }}>🎬 System Oceny Filmów</h1>
            <hr />

            {!token ? (
                <div style={{ background: '#ecf0f1', padding: '20px', borderRadius: '8px', marginBottom: '20px' }}>
                    <h3>🔐 Zaloguj się, aby dodać film</h3>
                    <form onSubmit={handleLogin}>
                        <input
                            style={{ padding: '8px', marginRight: '10px' }}
                            placeholder="Username"
                            onChange={e => setUser({ ...user, username: e.target.value })}
                        />
                        <input
                            style={{ padding: '8px', marginRight: '10px' }}
                            type="password"
                            placeholder="Password"
                            onChange={e => setUser({ ...user, password: e.target.value })}
                        />
                        <button style={{ padding: '8px 20px', cursor: 'pointer' }} type="submit">Zaloguj</button>
                    </form>
                </div>
            ) : (
                <div style={{ background: '#d5f5e3', padding: '20px', borderRadius: '8px', marginBottom: '20px' }}>
                    <h3>✅ Jesteś zalogowany!</h3>
                    <form onSubmit={handleAddMovie}>
                        <h4>Dodaj nowy film:</h4>
                        <input style={{ display: 'block', margin: '5px 0', width: '100%', padding: '8px' }} placeholder="Tytuł" value={newMovie.title} onChange={e => setNewMovie({ ...newMovie, title: e.target.value })} required />

                        <textarea style={{ display: 'block', margin: '5px 0', width: '100%', padding: '8px' }} placeholder="Opis filmu" value={newMovie.description} onChange={e => setNewMovie({ ...newMovie, description: e.target.value })} />

                        <input style={{ display: 'block', margin: '5px 0', width: '100%', padding: '8px' }} type="number" placeholder="Rok" value={newMovie.releaseYear} onChange={e => setNewMovie({ ...newMovie, releaseYear: e.target.value })} required />

                        <button style={{ padding: '10px 20px', background: '#27ae60', color: 'white', border: 'none', cursor: 'pointer', marginTop: '10px' }} type="submit">Dodaj Film</button>
                    </form>
                    <button onClick={handleLogout} style={{ marginTop: '20px', background: 'none', border: '1px solid red', color: 'red', cursor: 'pointer' }}>Wyloguj się</button>
                </div>
            )}

            <h2>🎥 Katalog Filmów</h2>
            <div style={{ display: 'grid', gap: '15px' }}>
                {movies.length > 0 ? movies.map(m => (
                    <div key={m.id} style={{ border: '1px solid #ddd', padding: '15px', borderRadius: '5px' }}>
                        <h3 style={{ margin: '0 0 10px 0' }}>{m.title}</h3>
                        <p style={{ color: '#7f8c8d' }}>{m.description}</p>
                        <p><strong>Rok wydania:</strong> {m.releaseYear}</p>
                        {}
                    </div>
                )) : <p>Ładowanie filmów lub brak filmów w bazie...</p>}
            </div>
        </div>
    );
}

export default App;