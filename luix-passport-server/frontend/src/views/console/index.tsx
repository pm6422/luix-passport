import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Activity, CreditCard, Key, Lock, Shield, User, Users } from "lucide-react";
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"

export default function ConsolePage() {
  const { loginUser } = useStore(loginUserStore)
  const isAdmin = loginUser?.isAdmin;

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Header */}
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold">Console</h1>
        <div className="flex items-center space-x-2">
          <User className="h-5 w-5" />
          <span className="bg-primary text-primary-foreground px-2 py-1 rounded-full text-xs">
            {loginUser?.roleIds?.map((roleId) => {
              return roleId.replace("ROLE_", "") + ", ";
            })}
          </span>
        </div>
      </div>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Logins</CardTitle>
            <Key className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">1,234</div>
            <p className="text-xs text-muted-foreground">+12% from last month</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Active Sessions</CardTitle>
            <Activity className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">42</div>
            <p className="text-xs text-muted-foreground">Currently active</p>
          </CardContent>
        </Card>

        {isAdmin && (
          <>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Total Users</CardTitle>
                <Users className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">568</div>
                <p className="text-xs text-muted-foreground">+5 new this week</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Security Events</CardTitle>
                <Shield className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">8</div>
                <p className="text-xs text-muted-foreground">Today</p>
              </CardContent>
            </Card>
          </>
        )}
      </div>

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

        {/* Quick Actions */}
        <div className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Quick Actions</CardTitle>
            </CardHeader>
            <CardContent className="grid gap-4">
              <Button variant="outline" className="w-full justify-start">
                <Lock className="mr-2 h-4 w-4" />
                Change Password
              </Button>
              <Button variant="outline" className="w-full justify-start">
                <Key className="mr-2 h-4 w-4" />
                Two-Factor Authentication
              </Button>
              {isAdmin && (
                <>
                  <Button variant="outline" className="w-full justify-start">
                    <Users className="mr-2 h-4 w-4" />
                    Manage Users
                  </Button>
                  <Button variant="outline" className="w-full justify-start">
                    <Shield className="mr-2 h-4 w-4" />
                    Security Settings
                  </Button>
                </>
              )}
            </CardContent>
          </Card>

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

      {/* Admin Only Section */}
      {isAdmin && (
        <div className="mt-8">
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
      )}
    </div>
  );
}