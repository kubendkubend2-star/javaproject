const API = '/api';
const content = document.querySelector('#content');
const dialog = document.querySelector('#record-dialog');
const form = document.querySelector('#record-form');
const formFields = document.querySelector('#form-fields');
const toastRegion = document.querySelector('#toast-region');
const crumb = document.querySelector('#crumb-current');
const state = { view: 'dashboard', records: [], editingId: null, filters: { q: '', priority: '', status: '' } };

const modules = {
  farmers: { title: 'Farmers', description: 'Manage farmer profiles and regional contacts.', endpoint: '/farmers', singular: 'Farmer', search: 'Search name, phone or district', columns: ['farmer', 'phone', 'district', 'livestockCount', 'status'], fields: [
    ['name', 'Full name', 'text', true], ['phone', 'Phone', 'tel', true], ['email', 'Email', 'email'], ['farmName', 'Farm name', 'text'], ['district', 'District', 'text'], ['state', 'State', 'text'], ['address', 'Address', 'textarea', false, true]
  ] },
  specialists: { title: 'Veterinary specialists', description: 'Keep specialist credentials and clinical focus current.', endpoint: '/specialists', singular: 'Specialist', search: 'Search specialist or specialty', columns: ['specialist', 'specialization', 'phone', 'qualification', 'status'], fields: [
    ['name', 'Full name', 'text', true], ['phone', 'Phone', 'tel', true], ['email', 'Email', 'email'], ['specialization', 'Specialization', 'text'], ['qualification', 'Qualification', 'text'], ['licenseNumber', 'License number', 'text'], ['yearsOfExperience', 'Years of experience', 'number'], ['consultationFee', 'Consultation fee (INR)', 'number']
  ] },
  vendors: { title: 'Pharma & lab vendors', description: 'Maintain pharmaceutical and diagnostic supplier contacts.', endpoint: '/vendors', singular: 'Vendor', search: 'Search company or contact', columns: ['vendor', 'vendorType', 'phone', 'district', 'status'], fields: [
    ['name', 'Contact name', 'text', true], ['companyName', 'Company name', 'text'], ['phone', 'Phone', 'tel', true], ['email', 'Email', 'email'], ['vendorType', 'Vendor type', 'select', false, false, ['PHARMACEUTICAL', 'LABORATORY', 'BOTH']], ['gstNumber', 'GST number', 'text'], ['district', 'District', 'text'], ['state', 'State', 'text'], ['address', 'Address', 'textarea', false, true]
  ] },
  livestock: { title: 'Livestock', description: 'Track registered animals and current health status.', endpoint: '/livestock', singular: 'Livestock', search: 'Search tag, species or farmer', columns: ['animal', 'species', 'farmer', 'age', 'healthStatus'], fields: [
    ['tagNumber', 'Tag number', 'text', true], ['animalName', 'Animal name', 'text'], ['species', 'Species', 'select', true, false, ['Cattle', 'Buffalo', 'Goat', 'Sheep', 'Poultry']], ['breed', 'Breed', 'text'], ['gender', 'Gender', 'select', false, false, ['Female', 'Male']], ['age', 'Age (years)', 'number'], ['weight', 'Weight (kg)', 'number'], ['farmerId', 'Farmer', 'farmer-select', true], ['healthStatus', 'Health status', 'select', false, false, ['HEALTHY', 'UNDER_TREATMENT', 'CRITICAL', 'RECOVERED']]
  ] },
  consultations: { title: 'Consultations', description: 'Triage, assign and follow remote clinical cases.', endpoint: '/televet/consultations', singular: 'Consultation', search: 'Search farmer, tag or consultation no.', columns: ['consultation', 'farmer', 'animal', 'veterinarian', 'priority', 'status'], fields: [
    ['farmerId', 'Farmer', 'farmer-select', true], ['livestockId', 'Livestock', 'livestock-select', true], ['veterinarianId', 'Veterinarian', 'specialist-select'], ['symptoms', 'Reported symptoms', 'textarea', true, true], ['consultationType', 'Consultation type', 'select', false, false, ['VIDEO', 'AUDIO', 'CHAT']], ['consultationFee', 'Consultation fee (INR)', 'number'], ['clinicalNotes', 'Clinical notes', 'textarea', false, true], ['diagnosis', 'Diagnosis', 'text'], ['followUpDate', 'Follow-up date', 'date']
  ] }
};

const labels = {
  farmer: 'Farmer', phone: 'Phone', district: 'District', livestockCount: 'Animals', status: 'Status',
  specialist: 'Specialist', specialization: 'Specialization', qualification: 'Qualification', vendor: 'Vendor',
  vendorType: 'Type', animal: 'Livestock', species: 'Species', age: 'Age', healthStatus: 'Health',
  consultation: 'Consultation', veterinarian: 'Veterinarian', priority: 'Priority',
};

function node(tag, className, text) {
  const element = document.createElement(tag);
  if (className) element.className = className;
  if (text !== undefined && text !== null) element.textContent = text;
  return element;
}

async function api(path, options = {}) {
  const response = await fetch(API + path, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) }
  });
  if (!response.ok) {
    const error = await response.json().catch(() => ({}));
    throw new Error(error.message || `Request failed (${response.status})`);
  }
  if (response.status === 204) return null;
  return response.json();
}

function showToast(message, error = false) {
  const toast = node('div', `toast${error ? ' error' : ''}`, message);
  toastRegion.append(toast);
  window.setTimeout(() => toast.remove(), 3400);
}

function setHeading(title, description, actionText, action) {
  const heading = node('div', 'page-heading');
  const copy = node('div');
  copy.append(node('p', 'eyebrow', 'FIELD OPERATIONS'), node('h1', '', title), node('p', 'page-description', description));
  heading.append(copy);
  if (actionText) {
    const button = node('button', 'button button-primary', `＋ ${actionText}`);
    button.type = 'button';
    button.addEventListener('click', action);
    heading.append(button);
  }
  return heading;
}

async function render() {
  state.view = state.view || 'dashboard';
  crumb.textContent = state.view === 'dashboard' ? 'Overview' : modules[state.view].title;
  document.querySelectorAll('.nav-item').forEach(button => button.classList.toggle('active', button.dataset.view === state.view));
  content.replaceChildren(node('div', 'loading', 'Loading workspace data…'));
  try {
    if (state.view === 'dashboard') await renderDashboard();
    else await renderModule(state.view);
  } catch (error) {
    content.replaceChildren(node('div', 'panel empty-state', error.message));
    showToast(error.message, true);
  }
}

async function renderDashboard() {
  const [stats, consults] = await Promise.all([api('/dashboard'), api('/televet/consultations')]);
  content.replaceChildren();
  content.append(setHeading('Good morning, team', 'A live view of care delivery across your regional network.', 'New consultation', () => openForm('consultations')));

  const metrics = [
    ['Farmers registered', stats.totalFarmers, 'Across the regional network', '♙'],
    ['Livestock tracked', stats.totalLivestock, `${stats.criticalLivestock} critical health cases`, '♧'],
    ['Active specialists', stats.activeVeterinarians, 'Available clinical team', '✚'],
    ['Awaiting attention', stats.pendingConsultations, `${stats.criticalConsultations} critical priority`, '✳']
  ];
  const grid = node('div', 'stats-grid');
  metrics.forEach(([label, value, note, mark]) => {
    const card = node('article', 'stat-card');
    card.append(node('span', 'stat-label', label), node('span', 'stat-mark', mark), node('p', 'stat-value', value), node('span', 'stat-note', note));
    grid.append(card);
  });
  content.append(grid);

  const dashboardGrid = node('div', 'dashboard-grid');
  const activityPanel = node('section', 'panel');
  const activityHead = node('div', 'panel-heading');
  activityHead.append(node('h2', '', 'Recent consultations'), node('span', '', `${consults.length} cases`));
  activityPanel.append(activityHead);
  const activityList = node('div', 'activity-list');
  consults.slice(0, 5).forEach(item => {
    const row = node('div', 'activity-item');
    row.append(node('span', 'activity-icon', '✳'));
    const copy = node('div', 'activity-copy');
    copy.append(node('strong', '', `${item.farmerName} · ${item.livestockTag}`), node('small', '', `${item.consultationNumber} · ${item.diagnosticPriority} priority`));
    row.append(copy, node('span', 'activity-time', formatDate(item.createdAt)));
    activityList.append(row);
  });
  if (!consults.length) activityList.append(node('div', 'empty-state', 'No consultations recorded yet.'));
  activityPanel.append(activityList);

  const priorityPanel = node('section', 'panel');
  const priorityHead = node('div', 'panel-heading');
  priorityHead.append(node('h2', '', 'Priority watch'), node('span', '', 'Needs clinical review'));
  priorityPanel.append(priorityHead);
  const priorityList = node('div', 'priority-list');
  const critical = consults.filter(item => ['CRITICAL', 'HIGH'].includes(item.diagnosticPriority)).slice(0, 5);
  critical.forEach(item => {
    const row = node('div', 'priority-case');
    const copy = node('div');
    copy.append(node('strong', '', item.farmerName), node('small', '', `${item.livestockTag} · ${item.status.replaceAll('_', ' ')}`));
    row.append(copy, badge(item.diagnosticPriority, true));
    priorityList.append(row);
  });
  if (!critical.length) priorityList.append(node('div', 'empty-state', 'No high-priority cases in the queue.'));
  priorityPanel.append(priorityList);
  dashboardGrid.append(activityPanel, priorityPanel);
  content.append(dashboardGrid);
}

async function renderModule(view) {
  const module = modules[view];
  const query = state.filters.q ? `?q=${encodeURIComponent(state.filters.q)}` : '';
  state.records = await api(module.endpoint + query);
  if (view === 'consultations') {
    state.records = state.records.filter(record => (!state.filters.priority || record.diagnosticPriority === state.filters.priority)
      && (!state.filters.status || record.status === state.filters.status)
      && (!state.filters.q || [record.consultationNumber, record.farmerName, record.livestockTag].some(value => value.toLowerCase().includes(state.filters.q.toLowerCase()))));
  }
  content.replaceChildren(setHeading(module.title, module.description, `Add ${module.singular.toLowerCase()}`, () => openForm(view)));
  const tools = node('div', 'section-tools');
  const searchWrap = node('label', 'search-box');
  searchWrap.setAttribute('aria-label', module.search);
  const search = document.createElement('input');
  search.placeholder = module.search;
  search.value = state.filters.q;
  search.addEventListener('input', debounce(() => { state.filters.q = search.value.trim(); render(); }, 240));
  searchWrap.append(search);
  tools.append(searchWrap);
  if (view === 'consultations') {
    tools.append(makeFilter('priority', 'All priorities', ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL']), makeFilter('status', 'All statuses', ['REQUESTED', 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED']));
  }
  content.append(tools);
  const panel = node('section', 'panel');
  const tableWrap = node('div', 'table-wrap');
  const table = document.createElement('table');
  const thead = document.createElement('thead');
  const headRow = document.createElement('tr');
  module.columns.forEach(column => headRow.append(node('th', '', labels[column] || column)));
  headRow.append(node('th', '', 'Actions'));
  thead.append(headRow);
  const tbody = document.createElement('tbody');
  state.records.forEach(record => tbody.append(buildRow(view, record, module)));
  table.append(thead, tbody);
  tableWrap.append(table);
  if (!state.records.length) tableWrap.append(node('div', 'empty-state', `No ${module.title.toLowerCase()} found. Add a record to get started.`));
  panel.append(tableWrap);
  const foot = node('div', 'table-foot');
  foot.append(node('span', '', `${state.records.length} record${state.records.length === 1 ? '' : 's'}`), node('span', '', 'Live database records'));
  panel.append(foot);
  content.append(panel);
}

function makeFilter(key, placeholder, options) {
  const select = node('select', 'filter-select');
  const first = node('option', '', placeholder);
  first.value = '';
  select.append(first);
  options.forEach(value => {
    const option = node('option', '', value.replaceAll('_', ' '));
    option.value = value;
    option.selected = state.filters[key] === value;
    select.append(option);
  });
  select.addEventListener('change', () => { state.filters[key] = select.value; render(); });
  return select;
}

function buildRow(view, record, module) {
  const row = document.createElement('tr');
  module.columns.forEach(column => {
    const cell = document.createElement('td');
    if (column === 'status' || column === 'healthStatus') cell.append(badge(record[column] || 'ACTIVE'));
    else if (column === 'priority') cell.append(badge(record.diagnosticPriority, true));
    else {
      const value = rowValue(column, record);
      if (['farmer', 'specialist', 'vendor', 'animal', 'consultation'].includes(column)) {
        const primary = node('span', 'table-primary', value || '—');
        cell.append(primary);
        const secondary = rowSecondary(column, record);
        if (secondary) cell.append(node('span', 'table-secondary', secondary));
      } else cell.textContent = value ?? '—';
    }
    row.append(cell);
  });
  const actions = node('td');
  const buttons = node('div', 'row-actions');
  const edit = node('button', 'text-button', 'Edit');
  edit.type = 'button'; edit.addEventListener('click', () => openForm(view, record));
  const remove = node('button', 'button button-danger button-small', 'Delete');
  remove.type = 'button'; remove.addEventListener('click', () => deleteRecord(view, record));
  buttons.append(edit, remove);
  if (view === 'consultations' && record.status !== 'COMPLETED' && record.status !== 'CANCELLED') {
    const complete = node('button', 'text-button', 'Complete');
    complete.type = 'button';
    complete.addEventListener('click', () => completeConsultation(record));
    buttons.append(complete);
  }
  actions.append(buttons); row.append(actions);
  return row;
}

function rowValue(column, record) {
  const values = {
    farmer: record.name, specialist: record.name, vendor: record.companyName || record.name,
    animal: record.animalName || record.tagNumber, consultation: record.consultationNumber,
    phone: record.phone, district: record.district, livestockCount: record.livestockCount ?? '—',
    specialization: record.specialization, qualification: record.qualification,
    vendorType: record.vendorType, species: record.species,
    age: record.age == null ? '—' : `${record.age} yrs`
  };
  return values[column] ?? '—';
}

function rowSecondary(column, record) {
  if (column === 'farmer') return record.farmName || record.email || '';
  if (column === 'specialist') return record.licenseNumber || record.email || '';
  if (column === 'vendor') return record.email || '';
  if (column === 'animal') return `${record.tagNumber} · ${record.breed || 'Breed not set'}`;
  if (column === 'consultation') return `${record.livestockTag} · ${formatDate(record.consultationDate)}`;
  return '';
}

function badge(value, priority = false) {
  const normalized = String(value || 'LOW').toLowerCase().replaceAll(' ', '_');
  const span = node('span', `${priority ? 'priority-pill priority-' : 'status-pill status-'}${normalized}`, String(value || 'LOW').replaceAll('_', ' '));
  return span;
}

async function openForm(view, record = null) {
  const module = modules[view];
  state.editingId = record?.id ?? null;
  document.querySelector('#dialog-eyebrow').textContent = module.singular.toUpperCase();
  document.querySelector('#dialog-title').textContent = `${record ? 'Edit' : 'Add'} ${module.singular.toLowerCase()}`;
  document.querySelector('#save-button').textContent = record ? 'Save changes' : 'Create record';
  formFields.replaceChildren();
  for (const [name, label, type, required, wide, options] of module.fields) {
    const field = node('div', `field${wide ? ' wide' : ''}`);
    const labelNode = node('label', '', label);
    labelNode.htmlFor = `field-${name}`;
    field.append(labelNode);
    const input = await createInput(view, name, type, options);
    input.id = `field-${name}`;
    input.name = name;
    input.required = Boolean(required);
    if (record && record[name] != null) input.value = name === 'consultationDate' ? record[name].slice(0, 16) : record[name];
    if (type === 'number') input.min = '0';
    field.append(input);
    formFields.append(field);
  }
  dialog.dataset.view = view;
  dialog.showModal();
}

async function createInput(view, name, type, options = []) {
  if (type === 'farmer-select' || type === 'specialist-select' || type === 'livestock-select') {
    const select = node('select');
    select.append(optionNode('', 'Select a record'));
    let records;
    if (type === 'farmer-select') records = await api('/farmers');
    else if (type === 'specialist-select') records = await api('/specialists');
    else records = await api('/livestock');
    if (type === 'livestock-select') {
      const farmerSelect = formFields.querySelector('[name="farmerId"]');
      const farmerId = farmerSelect?.value;
      records = farmerId ? records.filter(item => String(item.farmerId) === farmerId) : records;
    }
    records.forEach(item => select.append(optionNode(type === 'livestock-select' ? item.id : item.id,
      type === 'farmer-select' ? item.name : type === 'specialist-select' ? `${item.name} · ${item.specialization || 'General'}` : `${item.tagNumber} · ${item.species} · ${item.farmerName}`)));
    if (name === 'farmerId' && view === 'consultations') select.addEventListener('change', async () => refreshLivestockOptions());
    return select;
  }
  if (type === 'select') {
    const select = node('select');
    select.append(optionNode('', 'Select an option'));
    options.forEach(value => select.append(optionNode(value, value.replaceAll('_', ' '))));
    return select;
  }
  const input = type === 'textarea' ? node('textarea') : document.createElement('input');
  if (type !== 'textarea') input.type = type;
  if (name === 'consultationDate') input.type = 'datetime-local';
  if (name === 'followUpDate') input.type = 'date';
  return input;
}

function optionNode(value, text) {
  const option = node('option', '', text);
  option.value = value;
  return option;
}

async function refreshLivestockOptions() {
  const select = formFields.querySelector('[name="livestockId"]');
  const farmerId = formFields.querySelector('[name="farmerId"]')?.value;
  if (!select) return;
  const records = await api('/livestock');
  select.replaceChildren(optionNode('', 'Select livestock'));
  records.filter(item => !farmerId || String(item.farmerId) === farmerId)
    .forEach(item => select.append(optionNode(item.id, `${item.tagNumber} · ${item.species} · ${item.farmerName}`)));
}

form.addEventListener('submit', async event => {
  event.preventDefault();
  const view = dialog.dataset.view;
  const module = modules[view];
  const payload = Object.fromEntries(new FormData(form).entries());
  for (const [key, value] of Object.entries(payload)) if (value === '') payload[key] = null;
  ['farmerId', 'livestockId', 'veterinarianId', 'age', 'livestockCount', 'yearsOfExperience'].forEach(key => {
    if (payload[key] != null) payload[key] = Number(payload[key]);
  });
  ['consultationFee', 'weight'].forEach(key => { if (payload[key] != null) payload[key] = Number(payload[key]); });
  if (payload.consultationDate) payload.consultationDate = new Date(payload.consultationDate).toISOString();
  try {
    const path = `${module.endpoint}${state.editingId ? `/${state.editingId}` : ''}`;
    await api(path, { method: state.editingId ? 'PUT' : 'POST', body: JSON.stringify(payload) });
    dialog.close();
    showToast(`${module.singular} ${state.editingId ? 'updated' : 'created'} successfully`);
    state.filters.q = '';
    await render();
  } catch (error) { showToast(error.message, true); }
});

async function deleteRecord(view, record) {
  const module = modules[view];
  if (!window.confirm(`Delete this ${module.singular.toLowerCase()} record?`)) return;
  try {
    await api(`${module.endpoint}/${record.id}`, { method: 'DELETE' });
    showToast(`${module.singular} deleted`);
    await render();
  } catch (error) { showToast(error.message, true); }
}

async function completeConsultation(record) {
  const diagnosis = window.prompt('Enter the veterinarian diagnosis:', record.diagnosis || '');
  if (!diagnosis?.trim()) return;
  try {
    await api(`/televet/consultations/${record.id}/complete`, { method: 'POST', body: JSON.stringify({ diagnosis: diagnosis.trim(), clinicalNotes: record.clinicalNotes || null }) });
    showToast('Consultation marked complete');
    await render();
  } catch (error) { showToast(error.message, true); }
}

function formatDate(value) {
  if (!value) return 'Date not set';
  return new Intl.DateTimeFormat('en-IN', { day: 'numeric', month: 'short' }).format(new Date(value));
}

function debounce(callback, delay) {
  let timer;
  return (...args) => { window.clearTimeout(timer); timer = window.setTimeout(() => callback(...args), delay); };
}

document.querySelectorAll('[data-view]').forEach(button => button.addEventListener('click', event => {
  event.preventDefault();
  state.view = button.dataset.view;
  state.filters = { q: '', priority: '', status: '' };
  render();
}));
document.querySelector('#close-dialog').addEventListener('click', () => dialog.close());
document.querySelector('#cancel-dialog').addEventListener('click', () => dialog.close());
document.querySelector('#today-label').textContent = new Intl.DateTimeFormat('en-IN', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' }).format(new Date());
document.querySelector('#role-select').addEventListener('change', event => {
  showToast(`${event.target.options[event.target.selectedIndex].text} view selected`);
});
render();