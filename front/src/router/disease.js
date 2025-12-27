// src/router/disease.js
import DiseaseList from '@/pages/disease/DiseaseList.vue';
import MedicalAddForm from '@/pages/disease/MedicalAddForm.vue';

export default [
  {
    path: '/disease',
    name: 'disease-list',
    component: DiseaseList,
  },

  {
    path: '/disease/MedicalAddForm',
    name: 'disease-add',
    component: MedicalAddForm,
  },
  
];
