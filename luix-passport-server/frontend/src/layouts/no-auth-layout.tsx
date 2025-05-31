import { Outlet } from "react-router-dom"
import { Layout, LayoutHeader } from "@/layouts/layout-definitions"
import { Link } from "react-router-dom"

export default function NoAuthLayout() {
  return (
    <div className='relative h-full overflow-hidden bg-background'>
      <main id='content' className='h-full'>
        <Layout>
          <LayoutHeader>
            <div className='ml-auto flex items-center space-x-4'>
              <p className='mt-3 text-sm leading-relaxed text-gray-900'>
                <Link
                  to='/console'
                  className='font-bold text-blue-700 hover:underline'
                >
                  Console
                </Link>
              </p>
              <p className='mt-3 text-sm leading-relaxed text-gray-900'>
                <a
                  href='/login'
                  className='font-bold text-blue-700 hover:underline'
                >
                  Sign In
                </a>
              </p>
            </div>
          </LayoutHeader>
          {/* ===== View Content ===== */}
          <Outlet />
          {/* ===== View Content ===== */}
        </Layout>
      </main>
    </div>
  )
}