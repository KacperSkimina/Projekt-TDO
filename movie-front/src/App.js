import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

const API_URL = "https://rating-system-api-production.up.railway.app/api";

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
                // Czyszczenie pól po wysłaniu
                document.getElementById(`comment-${movieId}`).value = "";
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
                    <h2 style={{ textAlign: 'center', marginBottom: '20px' }}>Dołącz do nas</h2>
                    <div className="form-grid-auth">
                        <input placeholder="Użytkownik" onChange={e => setUser({ ...user, username: e.target.value })} />
                        <input type="email" placeholder="Email (do rejestracji)" onChange={e => setUser({ ...user, email: e.target.value })} />
                        <input type="password" placeholder="Hasło" onChange={e => setUser({ ...user, password: e.target.value })} />
                        <div className="auth-buttons">
                            <button type="button" onClick={handleLogin} className="btn-primary">ZALOGUJ</button>
                            <button type="button" onClick={handleRegister} className="btn-primary btn-secondary">REJESTRACJA</button>
                        </div>
                    </div>
                </div>
            ) : (
                <div className="panel">
                    <div className="panel-header">
                        <h2>Dodaj nowy film</h2>
                        <button onClick={handleLogout} className="btn-logout">Wyloguj</button>
                    </div>
                    <form onSubmit={handleAddMovie} className="form-grid-movie">
                        <input placeholder="Tytuł" value={newMovie.title} onChange={e => setNewMovie({ ...newMovie, title: e.target.value })} required />
                        <input type="number" placeholder="Rok" value={newMovie.releaseYear} onChange={e => setNewMovie({ ...newMovie, releaseYear: e.target.value })} required />
                        <textarea placeholder="Opis filmu..." value={newMovie.description} onChange={e => setNewMovie({ ...newMovie, description: e.target.value })} />
                        <button type="submit" className="btn-primary">DODAJ DO KATALOGU</button>
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
                                <h4>Recenzje:</h4>
                                {m.reviews && m.reviews.length > 0 ? m.reviews.map(rev => (
                                    <div key={rev.id} className="single-review">
                                        {/* LEWA KOLUMNA: Treść (Klasa 'review-content' spycha komentarz pod autora) */}
                                        <div className="review-content">
                                            <span className="review-author">{rev.username}</span>
                                            <p className="review-comment">{rev.comment}</p>
                                        </div>

                                        {/* PRAWA KOLUMNA: Ocena (Twoje gwiazdki) */}
                                        <div className="review-rating">
                                            {"★".repeat(rev.rating)}
                                            {"☆".repeat(10 - rev.rating)}
                                        </div>
                                    </div>
                                )) : <p className="no-reviews">Brak opinii.</p>}
                            </div>

                            {token && (
                                <div className="add-review-form">
                                    <div className="review-inputs" style={{ display: 'flex', gap: '5px' }}>
                                        <select id={`rating-${m.id}`} style={{ width: '60px' }}>
                                            {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map(n => <option key={n} value={n}>{n}★</option>)}
                                        </select>
                                        <input id={`comment-${m.id}`} placeholder="Twoja opinia..." style={{ flex: 1 }} />
                                    </div>
                                    <button className="btn-primary" style={{ marginTop: '5px', padding: '8px' }}
                                        onClick={() => handleAddReview(m.id, document.getElementById(`rating-${m.id}`).value, document.getElementById(`comment-${m.id}`).value)}>
                                        Dodaj opinię
                                    </button>
                                </div>
                            )}

                            <div className="movie-meta">
                                <span>{m.releaseYear}</span>
                                <span>★ Średnia: {m.averageRating ? m.averageRating.toFixed(1) : "brak"}</span>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default App;