async function sendRequest(method, url, body = null) {
    const options = { method };
    if (body) {
        options.headers = { 'Content-Type': 'application/json' };
        options.body = JSON.stringify(body);
    }
    const response = await fetch(url, options);
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.status === 204 ? null : await response.json();
}

function showModal(modal) {
    modal.classList.remove('hidden');
    setTimeout(() => modal.classList.add('show'), 10); // Небольшая задержка для анимации
}

function hideModal(modal) {
    modal.classList.remove('show');
    setTimeout(() => modal.classList.add('hidden'), 300); // Задержка соответствует времени анимации
}

async function fetchCounter() {
    try {
        const counter = await sendRequest('GET', '/api/counter');
        document.getElementById('counter').textContent = counter;
    } catch (error) {
        console.error('Ошибка при получении счётчика:', error);
    }
}