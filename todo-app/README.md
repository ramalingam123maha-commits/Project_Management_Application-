# To-Do List Web App

A clean, responsive To-Do List web application with zero external dependencies.

## Features

- **Add tasks**: Enter task text and press Enter or click Add.
- **Edit tasks**: Click the Edit button on any task to edit its title via an accessible `<dialog>` modal.
- **Delete tasks**: Remove tasks you no longer need.
- **Mark as completed**: Check the checkbox next to any task to toggle its completed state (visual strikethrough).
- **Persistent storage**: All tasks are stored in your browser's `localStorage` and persist across page refreshes.
- **XSS protected**: All task text is properly escaped before rendering.

## Files

- `index.html` - Semantic HTML structure including `<main>`, `<form>`, `<ul>`, and `<dialog>`.
- `styles.css` - Clean, responsive UI with modern typography and button styles.
- `app.js` - Lightweight vanilla JavaScript managing task state, CRUD operations, DOM events, and `localStorage` sync.
