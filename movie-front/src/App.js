import React, { useState, useEffect } from 'react';
import axios from 'axios';

const API_URL = "https://projekt-tdo-production.up.railway.app/api";

function App() {
    const [movies, setMovies] = useState([]);
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [user, setUser] = useState({ username: '', password: '', email: '' });
    const [newMovie, setNewMovie] = useState({ title: '', releaseYear: 2024, description: '' });

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

    const handleLogin = async (e) => {
        if (e) e.preventDefault();
        try {
            const res = await axios.post(`${API_URL}/auth/login`, {
                username: user.username,
                password: user.password
            });
            localStorage.setItem('token', res.data.token);
            setToken(res.data.token);
            alert("Zalogowano pomyślnie!");
        } catch (err) {
            alert("Błąd logowania! Sprawdź dane.");
        }
    };

    const handleRegister = async (e) => {
        if (e) e.preventDefault();
        try {
            await axios.post(`${API_URL}/auth/register`, user);
            alert("Konto utworzone! Teraz możesz się zalogować.");
        } catch (err) {
            alert("Błąd rejestracji! Email musi być poprawny, a hasło min. 6 znaków.");
        }
    };

    const handleAddMovie = async (e) => {
        e.preventDefault();
        try {
            await axios.post(`${API_URL}/movies`, newMovie, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setNewMovie({ title: '', releaseYear: 2024, description: '' });
            fetchMovies();
            alert("Film dodany!");
        } catch (err) {
            alert("Błąd: Sesja wygasła lub brak uprawnień (403).");
        }
    };

    const handleAddReview = async (movieId, rating, comment) => {
        if (!token) return;
        try {
            const response = await axios.post(`${API_URL}/reviews`,
                { movieId, rating: parseInt(rating), comment },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            if (response.status === 200 || response.status === 201) {
                alert("Recenzja dodana!");
                fetchMovies();
            }
        } catch (err) {
            alert("Nie udało się dodać recenzji.");
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
        setToken(null);
    };

    return (
        <div className="app-container">
            <h1 className="main-title">🎬 MOVIE BASE</h1>

            {!token ? (
                <div className="panel">
                    <h2 style={{ textAlign: 'center' }}>Zaloguj się lub załóż konto</h2>
                    <div className="form-grid" style={{ display: 'flex', flexDirection: 'column', maxWidth: '400px', margin: '0 auto', gap: '10px' }}>
                        <input placeholder="Użytkownik" onChange={e => setUser({ ...user, username: e.target.value })} />
                        <input type="email" placeholder="Email (do rejestracji)" onChange={e => setUser({ ...user, email: e.target.value })} />
                        <input type="password" placeholder="Hasło" onChange={e => setUser({ ...user, password: e.target.value })} />
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <button type="button" onClick={handleLogin} className="btn-primary" style={{ flex: 1 }}>ZALOGUJ</button>
                            <button type="button" onClick={handleRegister} className="btn-primary" style={{ flex: 1, backgroundColor: '#444' }}>REJESTRACJA</button>
                        </div>
                    </div>
                </div>
            ) : (
                <div className="panel">
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                        <h2>Dodaj nowy film</h2>
                        <button onClick={handleLogout} className="btn-logout">Wyloguj</button>
                    </div>
                    <form onSubmit={handleAddMovie} className="form-grid">
                        <input placeholder="Tytuł" value={newMovie.title} onChange={e => setNewMovie({ ...newMovie, title: e.target.value })} required />
                        <input type="number" placeholder="Rok" value={newMovie.releaseYear} onChange={e => setNewMovie({ ...newMovie, releaseYear: e.target.value })} required />
                        <textarea placeholder="Opis..." value={newMovie.description} onChange={e => setNewMovie({ ...newMovie, description: e.target.value })} />
                        <button type="submit" className="btn-primary">DODAJ</button>
                    </form>
                </div>
            )}

            <div className="movie-grid">
                {movies.map(m => (
                    <div key={m.id} className="movie-card">
                        <div className="movie-info">
                            <h3 className="movie-title">{m.title}</h3>
                            <p className="movie-description">{m.description}</p>

                            <div className="reviews-list">
                                <h4 style={{ color: '#e50914' }}>Recenzje:</h4>
                                {m.reviews && m.reviews.length > 0 ? m.reviews.map(rev => (
                                    <div key={rev.id} className="single-review">
                                        {/* Dopasowane do ReviewDTO: rev.username */}
                                        <strong>{rev.username}: </strong>
                                        <span>{rev.comment}</span>
                                        <span style={{ float: 'right', color: '#ffc107' }}>{rev.rating}/10 ★</span>
                                    </div>
                                )) : <p>Brak opinii.</p>}
                            </div>

                            {token && (
                                <div className="add-review-form">
                                    <select id={`rating-${m.id}`}>
                                        {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map(n => <option key={n} value={n}>{n}★</option>)}
                                    </select>
                                    <input id={`comment-${m.id}`} placeholder="Twoja recenzja..." />
                                    <button onClick={() => handleAddReview(m.id, document.getElementById(`rating-${m.id}`).value, document.getElementById(`comment-${m.id}`).value)}>Wyślij</button>
                                </div>
                            )}
                            <div className="movie-meta">
                                <span>{m.releaseYear}</span>
                                <span>★ Średnia: {m.averageRating || "brak"}</span>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default App;