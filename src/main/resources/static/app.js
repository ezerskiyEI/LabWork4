function initializeTabs() {
    const carsTab = document.getElementById('carsTab');
    const ownersTab = document.getElementById('ownersTab');
    const carsSection = document.getElementById('carsSection');
    const ownersSection = document.getElementById('ownersSection');

    carsTab.addEventListener('click', () => {
        carsTab.classList.add('active-tab');
        ownersTab.classList.remove('active-tab');
        carsSection.classList.remove('hidden');
        ownersSection.classList.add('hidden');
    });

    ownersTab.addEventListener('click', () => {
        ownersTab.classList.add('active-tab');
        carsTab.classList.remove('active-tab');
        ownersSection.classList.remove('hidden');
        carsSection.classList.add('hidden');
    });
}

// Логика для автомобилей
const carTableBody = document.getElementById('carTableBody');
const addCarBtn = document.getElementById('addCarBtn');
const carModal = document.getElementById('carModal');
const closeCarModal = document.getElementById('closeCarModal');
const carForm = document.getElementById('carForm');
const carFormTitle = document.getElementById('carFormTitle');
const analyzeBtn = document.getElementById('analyzeBtn');
const analyzeText = document.getElementById('analyzeText');
const analyzeResult = document.getElementById('analyzeResult');
const sortBy = document.getElementById('sortBy');
const sortBtn = document.getElementById('sortBtn');
const vinSearch = document.getElementById('vinSearch');
const searchBtn = document.getElementById('searchBtn');
const searchResult = document.getElementById('searchResult');
const error404 = document.getElementById('error404');
let editingCar = null;

async function fetchCars(sortField = null) {
    try {
        const cars = await sendRequest('GET', '/api/cars');
        let sortedCars = [...cars];
        if (sortField) {
            sortedCars.sort((a, b) => a[sortField].localeCompare(b[sortField]));
        }
        carTableBody.innerHTML = '';
        sortedCars.forEach(car => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td class="px-4 py-3 border-b border-green-300">${car.vin}</td>
                <td class="px-4 py-3 border-b border-green-300">${car.make}</td>
                <td class="px-4 py-3 border-b border-green-300">${car.model}</td>
                <td class="px-4 py-3 border-b border-green-300">${car.year}</td>
                <td class="px-4 py-3 border-b border-green-300"><span class="text-green-600">Владелец #${car.ownerId}</span></td>
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
        fetchCars(sortBy.value); // Перезагружаем с текущей сортировкой
    } catch (error) {
        console.error('Ошибка при сохранении автомобиля:', error);
        alert('Не удалось сохранить автомобиль');
    }
}

async function deleteCar(vin) {
    if (confirm('Вы уверены, что хотите удалить этот автомобиль?')) {
        try {
            await sendRequest('DELETE', `/api/cars/${vin}`);
            fetchCars(sortBy.value); // Перезагружаем с текущей сортировкой
        } catch (error) {
            console.error('Ошибка при удалении автомобиля:', error);
            alert('Не удалось удалить автомобиль');
        }
    }
}

async function searchByVin() {
    const vin = vinSearch.value.trim();
    if (!vin) {
        searchResult.innerHTML = '<div class="p-4 bg-red-100 rounded-lg">Пожалуйста, введите VIN</div>';
        return;
    }
    try {
        const car = await sendRequest('GET', `/api/cars/${vin}`);
        if (car) {
            searchResult.innerHTML = `
                <div class="p-4 bg-green-100 rounded-lg">
                    <p><strong>VIN:</strong> ${car.vin}</p>
                    <p><strong>Марка:</strong> ${car.make}</p>
                    <p><strong>Модель:</strong> ${car.model}</p>
                    <p><strong>Год:</strong> ${car.year}</p>
                    <p><strong>Владелец:</strong> #${car.ownerId}</p>
                </div>
            `;
            error404.classList.add('hidden');
        } else {
            searchResult.innerHTML = '';
            error404.classList.remove('hidden');
        }
    } catch (error) {
        console.error('Ошибка при поиске автомобиля:', error);
        searchResult.innerHTML = '';
        error404.classList.remove('hidden');
    }
}

// Логика для владельцев
const ownerTableBody = document.getElementById('ownerTableBody');
const addOwnerBtn = document.getElementById('addOwnerBtn');
const ownerModal = document.getElementById('ownerModal');
const closeOwnerModal = document.getElementById('closeOwnerModal');
const ownerForm = document.getElementById('ownerForm');
const ownerFormTitle = document.getElementById('ownerFormTitle');
let editingOwner = null;

async function fetchOwners() {
    try {
        const owners = await sendRequest('GET', '/api/owners');
        ownerTableBody.innerHTML = '';
        owners.forEach(owner => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td class="px-4 py-3 border-b border-green-300">${owner.id}</td>
                <td class="px-4 py-3 border-b border-green-300">${owner.name}</td>
                <td class="px-4 py-3 border-b border-green-300">
                    <button class="view-cars-btn text-green-600 hover:underline"><i class="fas fa-car mr-1"></i>Посмотреть автомобили</button>
                    <div class="cars-list hidden"></div>
                </td>
                <td class="px-4 py-3 border-b border-green-300">
                    <button class="edit-btn bg-yellow-500 text-white px-2 py-1 rounded-lg mr-2 hover:bg-yellow-600 transition"><i class="fas fa-edit mr-1"></i>Редактировать</button>
                    <button class="delete-btn bg-red-500 text-white px-2 py-1 rounded-lg hover:bg-red-600 transition"><i class="fas fa-trash mr-1"></i>Удалить</button>
                </td>
            `;
            row.querySelector('.edit-btn').addEventListener('click', () => openEditOwnerModal(owner));
            row.querySelector('.delete-btn').addEventListener('click', () => deleteOwner(owner.id));
            row.querySelector('.view-cars-btn').addEventListener('click', () => toggleCars(owner.id, row.querySelector('.cars-list')));
            ownerTableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Ошибка при получении владельцев:', error);
    }
}

async function toggleCars(ownerId, carsListDiv) {
    if (carsListDiv.classList.contains('hidden')) {
        try {
            const cars = await sendRequest('GET', `/api/cars/by-owner/${ownerId}`);
            carsListDiv.innerHTML = cars.length ? `
                <ul class="list-disc ml-4 text-gray-700">
                    ${cars.map(car => `<li>${car.make} ${car.model} (${car.year})</li>`).join('')}
                </ul>
            ` : '<p class="text-gray-700">Автомобили не найдены</p>';
            carsListDiv.classList.remove('hidden');
        } catch (error) {
            console.error('Ошибка при получении автомобилей владельца:', error);
            carsListDiv.innerHTML = '<p class="text-red-600">Ошибка загрузки автомобилей</p>';
            carsListDiv.classList.remove('hidden');
        }
    } else {
        carsListDiv.classList.add('hidden');
    }
}

function openAddOwnerModal() {
    editingOwner = null;
    ownerFormTitle.textContent = 'Добавить владельца';
    ownerForm.reset();
    document.getElementById('ownerId').disabled = false;
    showModal(ownerModal);
}

function openEditOwnerModal(owner) {
    editingOwner = owner;
    ownerFormTitle.textContent = 'Редактировать владельца';
    document.getElementById('ownerId').value = owner.id;
    document.getElementById('ownerId').disabled = true;
    document.getElementById('ownerName').value = owner.name;
    showModal(ownerModal);
}

async function saveOwner(event) {
    event.preventDefault();
    const owner = {
        id: parseInt(document.getElementById('ownerId').value),
        name: document.getElementById('ownerName').value
    };
    try {
        if (editingOwner) {
            await sendRequest('PUT', `/api/owners/${owner.id}`, owner);
        } else {
            await sendRequest('POST', '/api/owners', owner);
        }
        hideModal(ownerModal);
        fetchOwners();
    } catch (error) {
        console.error('Ошибка при сохранении владельца:', error);
        alert('Не удалось сохранить владельца');
    }
}

async function deleteOwner(id) {
    if (confirm('Вы уверены, что хотите удалить этого владельца?')) {
        try {
            await sendRequest('DELETE', `/api/owners/${id}`);
            fetchOwners();
        } catch (error) {
            console.error('Ошибка при удалении владельца:', error);
            alert('Не удалось удалить владельца');
        }
    }
}

// Привязка событий
addCarBtn.addEventListener('click', openAddCarModal);
closeCarModal.addEventListener('click', () => hideModal(carModal));
carForm.addEventListener('submit', saveCar);
analyzeBtn.addEventListener('click', analyzeTextFn);
sortBtn.addEventListener('click', () => fetchCars(sortBy.value));
searchBtn.addEventListener('click', searchByVin);

addOwnerBtn.addEventListener('click', openAddOwnerModal);
closeOwnerModal.addEventListener('click', () => hideModal(ownerModal));
ownerForm.addEventListener('submit', saveOwner);