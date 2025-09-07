import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import PeopleView from '@/views/people/PeopleView.vue'
import StatisticsView from '@/views/statistics/StatisticsView.vue'
import MaterialsView from '@/views/curriculumunits/units/materials/MaterialsView.vue'
import UnitLayout from '../views/curriculumunits/units/UnitLayout.vue'
import SubmissionsView from '../views/curriculumunits/units/Projects/SubmissionsView.vue'
import PersonnelView from '../views/curriculumunits/units/personnel/PersonnelView.vue'
import CoursesView from '@/views/courses/CoursesView.vue'
import CurriculumUnitsView from '@/views/curriculumunits/CurriculumUnitsView.vue'
import UnitView from '../views/curriculumunits/units/UnitView.vue'
import ProjectsView from '@/views/curriculumunits/units/Projects/ProjectsView.vue'
import TestsView from '@/views/curriculumunits/units/tests/TestsView.vue'
import StudentTestsView from '@/views/curriculumunits/units/tests/StudentTestsView.vue'
import RevisionsView from '@/views/curriculumunits/units/tests/RevisionsView.vue'
import StudentProfileView from '@/views/people/students/StudentProfileView.vue'
import StudentDeadlinesView from '@/views/people/students/StudentDeadlinesView.vue'
import StudentProgressView from '@/views/people/students/StudentProgressView.vue'
import StudentsView from '@/views/people/teachers/StudentsView.vue'
import TeacherPendingAssessments from '@/views/people/teachers/TeacherPendingAssessments.vue'
import TeacherStatistics from '@/views/people/teachers/TeacherStatistics.vue'
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
    // Student dashboard routes
    {
      path: '/profile',
      name: 'profile',
      component: StudentProfileView
    },
    {
      path: '/deadlines',
      name: 'deadlines',
      component: StudentDeadlinesView
    },
    {
      path: '/progress',
      name: 'progress',
      component: StudentProgressView
    },
    // Teacher dashboard routes
    {
      path: '/students',
      name: 'students',
      component: StudentsView
    },
    {
      path: '/pending-corrections',
      name: 'pending-corrections',
      component: TeacherPendingAssessments
    },
    {
      path: '/correction-tasks',
      name: 'correction-tasks',
      component: TeacherPendingAssessments
    },
    {
      path: '/main-statistics',
      name: 'main-statistics',
      component: TeacherStatistics
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
        {
        path: 'projects',
        name: 'ProjectsView',
        component: ProjectsView,
        },
        {
        path: 'materials',
        name: 'MaterialsView',
        component: MaterialsView,
        },
        {
        path: 'projects/:projectId/submissions',
        name: 'project-submissions',
        component: SubmissionsView,
        },
        {
        path: '/curriculum-units/:id/tests',
        name: 'TestsView',
        component: TestsView,
        },
        {
          path: 'tests/:testId/students',
          name: 'StudentTestsView',
          component: StudentTestsView,
          props: true,
        },
        {
          path: 'revisions',
          name: 'RevisionsView',
          component: RevisionsView,
        }
      ]
    },
  ]
})

export default router
