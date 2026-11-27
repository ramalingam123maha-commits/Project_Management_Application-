// Todo App - Add, Edit, Delete, Mark Completed, localStorage persistence
(function () {
  'use strict';

  const STORAGE_KEY = 'todo_app_tasks';

  let tasks = loadTasks();
  let currentEditingId = null;

  // DOM Elements
  const taskForm = document.getElementById('task-form');
  const taskInput = document.getElementById('task-input');
  const taskList = document.getElementById('task-list');
  const emptyState = document.getElementById('empty-state');

  const editDialog = document.getElementById('edit-dialog');
  const editForm = document.getElementById('edit-form');
  const editInput = document.getElementById('edit-input');
  const cancelEditBtn = document.getElementById('cancel-edit');

  function loadTasks() {
    try {
      const data = localStorage.getItem(STORAGE_KEY);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  }

  function saveTasks() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(tasks));
  }

  function addTask(text) {
    const trimmed = text.trim();
    if (!trimmed) return;

    tasks.push({
      id: Date.now().toString(36) + Math.random().toString(36).slice(2, 7),
      text: trimmed,
      completed: false
    });

    saveTasks();
    render();
  }

  function editTask(id, newText) {
    const trimmed = newText.trim();
    if (!trimmed) return;

    const task = tasks.find((t) => t.id === id);
    if (!task) return;

    task.text = trimmed;
    saveTasks();
    render();
  }

  function deleteTask(id) {
    tasks = tasks.filter((t) => t.id !== id);
    saveTasks();
    render();
  }

  function toggleCompleted(id) {
    const task = tasks.find((t) => t.id === id);
    if (!task) return;

    task.completed = !task.completed;
    saveTasks();
    render();
  }

  function escapeHTML(str) {
    return str.replace(/[&<>'"]/g, (tag) => ({
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      "'": '&#39;',
      '"': '&quot;'
    }[tag] || tag));
  }

  function render() {
    if (tasks.length === 0) {
      taskList.innerHTML = '';
      emptyState.hidden = false;
      return;
    }

    emptyState.hidden = true;

    taskList.innerHTML = tasks
      .map(
        (t) => `
        <li class="task-item ${t.completed ? 'completed' : ''}" data-id="${t.id}">
          <div class="task-item-left">
            <input type="checkbox" ${t.completed ? 'checked' : ''} />
            <span class="task-text">${escapeHTML(t.text)}</span>
          </div>
          <div class="task-actions">
            <button type="button" class="btn-edit">Edit</button>
            <button type="button" class="btn-delete">Delete</button>
          </div>
        </li>
      `
      )
      .join('');
  }

  // Event Listeners
  taskForm.addEventListener('submit', (e) => {
    e.preventDefault();
    addTask(taskInput.value);
    taskInput.value = '';
    taskInput.focus();
  });

  taskList.addEventListener('click', (e) => {
    const item = e.target.closest('.task-item');
    if (!item) return;
    const id = item.dataset.id;

    if (e.target.matches('input[type="checkbox"]')) {
      toggleCompleted(id);
      return;
    }

    if (e.target.matches('.btn-delete')) {
      deleteTask(id);
      return;
    }

    if (e.target.matches('.btn-edit')) {
      const task = tasks.find((t) => t.id === id);
      if (!task) return;
      currentEditingId = id;
      editInput.value = task.text;
      if (typeof editDialog.showModal === 'function') {
        editDialog.showModal();
      } else {
        // Fallback for older browsers
        const promptVal = prompt('Edit task:', task.text);
        if (promptVal !== null) {
          editTask(id, promptVal);
        }
      }
    }
  });

  editForm.addEventListener('submit', (e) => {
    e.preventDefault();
    if (currentEditingId) {
      editTask(currentEditingId, editInput.value);
      currentEditingId = null;
    }
    editDialog.close();
  });

  cancelEditBtn.addEventListener('click', () => {
    currentEditingId = null;
    editDialog.close();
  });

  // Initial render
  render();
})();
