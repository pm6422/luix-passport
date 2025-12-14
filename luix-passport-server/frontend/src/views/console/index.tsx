import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Activity, CreditCard, Lock, Shield, User, Mail } from "lucide-react"
import { RoleAdmin } from "@/permission/role-control"
import { LayoutBody } from "@/layouts/layout-definitions"
import { useEffect, useState } from "react"
import { TeamService, TeamInvitation } from "@/services/team-service"
import { toast } from "sonner"
import { Badge } from "@/components/ui/badge"

export default function ConsolePage() {
  const [invitations, setInvitations] = useState<TeamInvitation[]>([])
  const [loadingInvitations, setLoadingInvitations] = useState(true)

  useEffect(() => {
    loadInvitations()
  }, [])

  const loadInvitations = async () => {
    try {
      const { data } = await TeamService.getUserInvitations()
      setInvitations(data)
    } catch (error) {
      console.error("Failed to load invitations:", error)
    } finally {
      setLoadingInvitations(false)
    }
  }

  const handleAcceptInvitation = async (invitationId: string) => {
    try {
      await TeamService.acceptInvitation(invitationId)
      toast.success("Invitation accepted successfully")
      loadInvitations()
    } catch (error) {
      toast.error("Failed to accept invitation")
      console.error(error)
    }
  }

  const handleRejectInvitation = async (invitationId: string) => {
    try {
      await TeamService.rejectInvitation(invitationId)
      toast.success("Invitation rejected")
      loadInvitations()
    } catch (error) {
      toast.error("Failed to reject invitation")
      console.error(error)
    }
  }

  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      <div>
        {/* Header */}
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold">Console</h1>
        </div>

        {/* Admin Only Section */}
        <RoleAdmin>
          <div className="mb-8">
            <Card>
              <CardHeader>
                <CardTitle>Admin Console</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                  <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                      <CardTitle className="text-sm font-medium">Pending Approvals</CardTitle>
                      <CreditCard className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold">3</div>
                      <Button variant="link" size="sm" className="p-0 h-auto">View requests</Button>
                    </CardContent>
                  </Card>

                  <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                      <CardTitle className="text-sm font-medium">Recent Signups</CardTitle>
                      <User className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold">12</div>
                      <p className="text-xs text-muted-foreground">Last 7 days</p>
                    </CardContent>
                  </Card>

                  <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                      <CardTitle className="text-sm font-medium">Failed Logins</CardTitle>
                      <Lock className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold">7</div>
                      <p className="text-xs text-muted-foreground">Today</p>
                    </CardContent>
                  </Card>

                  <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                      <CardTitle className="text-sm font-medium">System Status</CardTitle>
                      <Activity className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold text-green-500">Normal</div>
                      <p className="text-xs text-muted-foreground">All systems operational</p>
                    </CardContent>
                  </Card>
                </div>
              </CardContent>
            </Card>
          </div>
        </RoleAdmin>

        {/* Main Content */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Recent Activity - Visible to all */}
          <div className="lg:col-span-2">
            <Card>
              <CardHeader>
                <CardTitle>Recent Activity</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {[1, 2, 3, 4].map((item) => (
                    <div key={item} className="flex items-center space-x-4 p-2 hover:bg-muted/50 rounded">
                      <div className="bg-primary/10 p-2 rounded-full">
                        <Activity className="h-4 w-4 text-primary" />
                      </div>
                      <div className="flex-1">
                        <p className="text-sm font-medium">Login from {item === 1 ? "your current device" : "new device"}</p>
                        <p className="text-sm text-muted-foreground">
                          {item === 1 ? "Just now" : `${item} hours ago`} • {item === 1 ? "Chrome, Windows" : "Safari, MacOS"}
                        </p>
                      </div>
                      <Button variant="outline" size="sm">Details</Button>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Team Invitations */}
          <div className="space-y-4">
            {!loadingInvitations && invitations.length > 0 && (
              <Card>
                <CardHeader>
                  <div className="flex items-center justify-between">
                    <CardTitle>Team Invitations</CardTitle>
                    <Badge className="bg-green-600/10 dark:bg-green-600/20 hover:bg-green-600/10 text-green-500 shadow-none rounded-full mx-1 animate-pulse">
                      {invitations.length}
                    </Badge>
                  </div>
                </CardHeader>
                <CardContent className="space-y-4">
                  {invitations.map((invitation) => (
                    <div key={invitation.id} className="border rounded-lg p-3 space-y-2">
                      <div className="flex items-start space-x-2">
                        <div className="bg-blue-500/10 p-1.5 rounded-full">
                          <Mail className="h-4 w-4 text-blue-500" />
                        </div>
                        <div className="flex-1 min-w-0">
                          <p className="text-sm font-medium">Team Invitation</p>
                          <p className="text-xs text-muted-foreground truncate">
                            Role: {invitation.memberRole}
                          </p>
                        </div>
                      </div>
                      <div className="flex gap-2">
                        <Button
                          size="sm"
                          className="flex-1"
                          onClick={() => handleAcceptInvitation(invitation.id)}
                        >
                          Accept
                        </Button>
                        <Button
                          size="sm"
                          variant="outline"
                          className="flex-1"
                          onClick={() => handleRejectInvitation(invitation.id)}
                        >
                          Reject
                        </Button>
                      </div>
                    </div>
                  ))}
                </CardContent>
              </Card>
            )}

            {/* Security Status */}
            <Card>
              <CardHeader>
                <CardTitle>Security Status</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-2">
                      <div className="bg-green-500/10 p-1 rounded-full">
                        <Shield className="h-4 w-4 text-green-500" />
                      </div>
                      <span>Password Strength</span>
                    </div>
                    <span className="text-sm font-medium text-green-500">Strong</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-2">
                      <div className="bg-blue-500/10 p-1 rounded-full">
                        <Lock className="h-4 w-4 text-blue-500" />
                      </div>
                      <span>2FA Status</span>
                    </div>
                    <span className="text-sm font-medium text-blue-500">Active</span>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </LayoutBody>
  );
}
