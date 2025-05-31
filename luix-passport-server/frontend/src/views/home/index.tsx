import { LayoutBody } from "@/layouts/layout-definitions"

export default function Home() {
  return (
    <LayoutBody className="space-y-4">
      <div className="flex items-center justify-between space-y-2">
        <h1 className="text-2xl font-bold tracking-tight md:text-3xl">
          Home
        </h1>
      </div>
    </LayoutBody>
  )
}
