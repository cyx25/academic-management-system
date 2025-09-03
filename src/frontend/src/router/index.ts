import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import PeopleView from '@/views/people/PeopleView.vue'
import StatisticsView from '@/views/statistics/StatisticsView.vue'
import UnitLayout from '@/views/curriculumunits/UnitLayout.vue'
import PersonnelView from '@/views/curriculumunits/PersonnelView.vue'
import CoursesView from '@/views/courses/CoursesView.vue'
import CurriculumUnitsView from '@/views/curriculumunits/CurriculumUnitsView.vue'
import UnitView from '@/views/curriculumunits/UnitView.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/people',
      name: 'people',
      component: PeopleView
    },
    {
      path: '/statistics',
      name: 'statistics',
      component: StatisticsView
    },
    {
      path: '/courses',
      name: 'courses',
      component: CoursesView
    },
    {
      path: '/curriculum-units',
      name: 'curriculum-units',
      component: CurriculumUnitsView
    },
    {
      path: '/curriculum-units/:id',
      component: UnitLayout,
      children: [
        {
          path: '',
          name: 'UnitView',
          component: UnitView,
        },
        {
          path: 'people',
          name: 'PersonnelView',
          component: PersonnelView,
        },
        // Future routes like 'projects', 'tests' can be added here
      ]
    },
  ]
})

export default router
