document.addEventListener('DOMContentLoaded', () => {
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

    addOwnerBtn.addEventListener('click', openAddOwnerModal);
    closeOwnerModal.addEventListener('click', () => hideModal(ownerModal));
    ownerForm.addEventListener('submit', saveOwner);

    fetchOwners();
});