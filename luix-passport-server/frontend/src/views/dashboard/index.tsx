import {
  Card,
  CardContent,
  // CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { useEffect, useState } from "react"
import { Tabs, TabsContent } from "@/components/ui/tabs"
import { LayoutBody } from "@/layouts/layout-definitions"
import { RecentSales } from "./components/recent-sales"
import { Overview } from "./components/overview"
import { IconUsers, IconShieldCheckered, IconVector, IconUserPlus } from "@tabler/icons-react"
import { UserService } from "@/services/user-service"
import { Oauth2ClientService } from "@/services/oauth2-client-service"
import { OrgService } from "@/services/org-service"
// import { SpringSessionService } from "@/services/spring-session-service"
// import { type SpringSession } from "@/domains/spring-session"

export default function Dashboard() {
  const [userCount, setUserCount] = useState(0)
  const [oauth2ClientCount, setOauth2ClientCount] = useState(0)
  const [orgCount, setOrgCount] = useState(0)
  // const [springSessions, setSpringSessions] = useState([] as Array<SpringSession>)

  useEffect(() => {
    UserService.count().then(r => {
      setUserCount(r.data)
    })

    Oauth2ClientService.count().then(r => {
      setOauth2ClientCount(r.data)
    })

    OrgService.count().then(r => {
      setOrgCount(r.data)
    })

    // SpringSessionService.findAll().then(r => {
    //   setSpringSessions(r.data)
    // })
  }, [])

  return (
    <LayoutBody className="space-y-4">
      <div className="flex items-center justify-between space-y-2">
        <h1 className="text-2xl font-bold tracking-tight md:text-3xl">
          Dashboard
        </h1>
        {/*<div className="flex items-center space-x-2">*/}
        {/*  <Button>Download</Button>*/}
        {/*</div>*/}
      </div>
      <Tabs
        orientation="vertical"
        defaultValue="overview"
        className="space-y-4"
      >
        {/*<div className="w-full pb-2">*/}
        {/*  <TabsList>*/}
        {/*    <TabsTrigger value="overview">Overview</TabsTrigger>*/}
        {/*    <TabsTrigger value="analytics">Analytics</TabsTrigger>*/}
        {/*  </TabsList>*/}
        {/*</div>*/}
        <TabsContent value="overview" className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">
                  Total Users
                </CardTitle>
                <IconUsers className="size-4"/>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{userCount}</div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">
                  Total Roles
                </CardTitle>
                <IconUserPlus className="size-4"/>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">4</div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">
                  Total Organizations
                </CardTitle>
                <IconVector className="size-4"/>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{orgCount}</div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">
                  Total oAuth2 Clients
                </CardTitle>
                <IconShieldCheckered className="size-4"/>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{oauth2ClientCount}</div>
              </CardContent>
            </Card>
          </div>
          <div className="grid grid-cols-1 gap-4 lg:grid-cols-7">
            <Card className="col-span-1 lg:col-span-4">
              <CardHeader>
                <CardTitle>Overview</CardTitle>
              </CardHeader>
              <CardContent className="pl-2">
                <Overview />
              </CardContent>
            </Card>
            <Card className="col-span-1 lg:col-span-3">
              <CardHeader>
                <CardTitle>Recent Login</CardTitle>
                {/*<CardDescription>*/}
                {/*  You made 265 sales this month.*/}
                {/*</CardDescription>*/}
              </CardHeader>
              <CardContent>
                <RecentSales />
              </CardContent>
            </Card>
          </div>
        </TabsContent>
      </Tabs>
    </LayoutBody>
  )
}
