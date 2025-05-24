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
    modal.classList.add('flex');
}

function hideModal(modal) {
    modal.classList.remove('flex');
    modal.classList.add('hidden');
}

async function fetchCounter() {
    try {
        const counter = await sendRequest('GET', '/api/counter');
        document.getElementById('counter').textContent = counter;
    } catch (error) {
        console.error('Ошибка при получении счётчика:', error);
    }
}