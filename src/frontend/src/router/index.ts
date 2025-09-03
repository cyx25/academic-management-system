import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import PeopleView from '@/views/people/PeopleView.vue'
import StatisticsView from '@/views/statistics/StatisticsView.vue'
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
      name: 'CurriculumUnitDetail',
      component: UnitView,
      props: true
    }
  ]
})

export default router
