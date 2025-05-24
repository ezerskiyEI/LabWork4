document.addEventListener('DOMContentLoaded', () => {
    const carTableBody = document.getElementById('carTableBody');
    const addCarBtn = document.getElementById('addCarBtn');
    const carModal = document.getElementById('carModal');
    const closeCarModal = document.getElementById('closeCarModal');
    const carForm = document.getElementById('carForm');
    const carFormTitle = document.getElementById('carFormTitle');
    const analyzeBtn = document.getElementById('analyzeBtn');
    const analyzeText = document.getElementById('analyzeText');
    const analyzeResult = document.getElementById('analyzeResult');
    let editingCar = null;

    async function fetchCars() {
        try {
            const cars = await sendRequest('GET', '/api/cars');
            carTableBody.innerHTML = '';
            cars.forEach(car => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td class="px-4 py-3 border-b border-green-300">${car.vin}</td>
                    <td class="px-4 py-3 border-b border-green-300">${car.make}</td>
                    <td class="px-4 py-3 border-b border-green-300">${car.model}</td>
                    <td class="px-4 py-3 border-b border-green-300">${car.year}</td>
                    <td class="px-4 py-3 border-b border-green-300"><a href="/owners" class="text-green-600 hover:underline">Владелец #${car.ownerId}</a></td>
                    <td class="px-4 py-3 border-b border-green-300">
                        <button class="edit-btn bg-yellow-500 text-white px-2 py-1 rounded-lg mr-2 hover:bg-yellow-600 transition"><i class="fas fa-edit mr-1"></i>Редактировать</button>
                        <button class="delete-btn bg-red-500 text-white px-2 py-1 rounded-lg hover:bg-red-600 transition"><i class="fas fa-trash mr-1"></i>Удалить</button>
                    </td>
                `;
                row.querySelector('.edit-btn').addEventListener('click', () => openEditCarModal(car));
                row.querySelector('.delete-btn').addEventListener('click', () => deleteCar(car.vin));
                carTableBody.appendChild(row);
            });
        } catch (error) {
            console.error('Ошибка при получении автомобилей:', error);
        }
    }

    function openAddCarModal() {
        editingCar = null;
        carFormTitle.textContent = 'Добавить автомобиль';
        carForm.reset();
        document.getElementById('carVin').disabled = false;
        showModal(carModal);
    }

    function openEditCarModal(car) {
        editingCar = car;
        carFormTitle.textContent = 'Редактировать автомобиль';
        document.getElementById('carVin').value = car.vin;
        document.getElementById('carVin').disabled = true;
        document.getElementById('carMake').value = car.make;
        document.getElementById('carModel').value = car.model;
        document.getElementById('carYear').value = car.year;
        document.getElementById('carOwnerId').value = car.ownerId;
        showModal(carModal);
    }

    async function saveCar(event) {
        event.preventDefault();
        const car = {
            vin: document.getElementById('carVin').value,
            make: document.getElementById('carMake').value,
            model: document.getElementById('carModel').value,
            year: parseInt(document.getElementById('carYear').value),
            ownerId: parseInt(document.getElementById('carOwnerId').value)
        };
        try {
            if (editingCar) {
                await sendRequest('PUT', `/api/cars/${car.vin}`, car);
            } else {
                await sendRequest('POST', '/api/cars', car);
            }
            hideModal(carModal);
            fetchCars();
        } catch (error) {
            console.error('Ошибка при сохранении автомобиля:', error);
            alert('Не удалось сохранить автомобиль');
        }
    }

    async function deleteCar(vin) {
        if (confirm('Вы уверены, что хотите удалить этот автомобиль?')) {
            try {
                await sendRequest('DELETE', `/api/cars/${vin}`);
                fetchCars();
            } catch (error) {
                console.error('Ошибка при удалении автомобиля:', error);
                alert('Не удалось удалить автомобиль');
            }
        }
    }

    async function analyzeTextFn() {
        const text = analyzeText.value.trim();
        if (!text) {
            analyzeResult.innerHTML = '<div class="p-4 bg-red-100 rounded-lg">Пожалуйста, введите текст для анализа</div>';
            return;
        }
        try {
            const result = await sendRequest('GET', `/api/cars/analyze?text=${encodeURIComponent(text)}`);
            if (result) {
                analyzeResult.innerHTML = `
                    <div class="p-4 bg-green-100 rounded-lg">
                        <p><strong>VIN:</strong> ${result.vin}</p>
                        <p><strong>Марка:</strong> ${result.make}</p>
                        <p><strong>Модель:</strong> ${result.model}</p>
                        <p><strong>Год:</strong> ${result.year}</p>
                    </div>
                `;
            } else {
                analyzeResult.innerHTML = '<div class="p-4 bg-red-100 rounded-lg">Неверный формат текста</div>';
            }
        } catch (error) {
            console.error('Ошибка при анализе текста:', error);
            analyzeResult.innerHTML = '<div class="p-4 bg-red-100 rounded-lg">Неверный формат текста</div>';
        }
    }

    addCarBtn.addEventListener('click', openAddCarModal);
    closeCarModal.addEventListener('click', () => hideModal(carModal));
    carForm.addEventListener('submit', saveCar);
    analyzeBtn.addEventListener('click', analyzeTextFn);

    fetchCars();
});