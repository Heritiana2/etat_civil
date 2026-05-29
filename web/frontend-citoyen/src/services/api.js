import axios from 'axios';

const API_URL = '/api';

const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' }
});

export async function loginCitoyen(identifiant) {
  const res = await api.post('/auth/citoyen', { identifiant });
  return res.data;
}

export async function getCitoyen(id) {
  const res = await api.get(`/auth/citoyen/${id}`);
  return res.data;
}

export async function creerDemande(data) {
  const res = await api.post('/demandes/creer', data);
  return res.data;
}

export async function getDemandes() {
  const res = await api.get('/demandes');
  return res.data;
}

export async function getDemandesByCitoyen(idCitoyen) {
  const res = await api.get(`/demandes/citoyen/${idCitoyen}`);
  return res.data;
}

export async function getSuivi(idDemande) {
  const res = await api.get(`/suivi/${idDemande}`);
  return res.data;
}

export async function telechargerActe(idActe) {
  const res = await api.get(`/documents/telecharger/${idActe}`);
  return res.data;
}

export async function verifierDocument(hash) {
  const res = await api.get(`/documents/verifier/${hash}`);
  return res.data;
}

export async function healthCheck() {
  const res = await api.get('/health');
  return res.data;
}

export default api;
