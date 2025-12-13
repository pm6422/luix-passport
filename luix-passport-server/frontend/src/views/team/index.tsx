import { useParams, Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { LayoutBody } from "@/layouts/layout-definitions"
import { IconUsers } from "@tabler/icons-react"

export default function TeamDashboard() {
  const { teamId } = useParams()

  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      <div className="flex items-center justify-between p-4 border-b bg-background">
        <h1 className="text-2xl font-bold tracking-tight">Team Dashboard</h1>
      </div>

      <div className="flex-1 p-4">
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <div className="rounded-xl border bg-card text-card-foreground shadow">
            <div className="p-6 flex flex-row items-center justify-between space-y-0 pb-2">
              <h3 className="tracking-tight text-sm font-medium">Team Members</h3>
              <IconUsers className="size-4 text-muted-foreground" />
            </div>
            <div className="p-6 pt-0">
              <div className="text-2xl font-bold">Manage</div>
              <p className="text-xs text-muted-foreground">
                View and manage team members
              </p>
              <div className="mt-4">
                <Link to={`/teams/${teamId}/members`}>
                  <Button variant="outline" className="w-full">
                    Go to Members
                  </Button>
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </LayoutBody>
  )
}
