import { LayoutBody } from "@/layouts/layout-definitions"
import { useEffect, useState } from "react"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { IconRefresh, IconEye } from "@tabler/icons-react"
import { Drawer, DrawerContent, DrawerHeader, DrawerTitle } from "@/components/ui/drawer"
import { ManagementService } from "@/services/management-service"

interface HealthData {
  name: string
  status: "UP" | "DOWN" | "UNKNOWN"
  details?: Record<string, unknown>
  error?: string
}

interface HealthComponent {
  status?: "UP" | "DOWN" | "UNKNOWN"
  components?: Record<string, HealthComponent>
  details?: Record<string, unknown>
  error?: string
  [key: string]: unknown
}

export default function HealthChecksPage() {
  const [healthList, setHealthList] = useState<HealthData[]>([])
  const [modalData, setModalData] = useState<Record<string, unknown>>({})
  const [showDrawer, setShowDrawer] = useState(false)

  const refresh = async () => {
    ManagementService.getHealth()
      .then(res => {
        setHealthList(transformHealthData(res.data))
      })
      .catch(error => {
        if (error.response?.status === 503) {
          setHealthList(transformHealthData(error.response.data))
        } else {
          setHealthList([{
            name: "Health API",
            status: "DOWN",
            error: error.message
          }])
        }
      })
  }

  const showDetails = (data: HealthData) => {
    setModalData({
      ...data.details,
      ...(data.error && { error: data.error })
    })
    setShowDrawer(true)
  }

  const transformHealthData = (data: { components: Record<string, HealthComponent> }): HealthData[] => {
    const response: HealthData[] = []
    flattenHealthData(response, null, data.components)
    return response
  }

  const flattenHealthData = (
    result: HealthData[],
    path: string | null,
    data: Record<string, HealthComponent>
  ): void => {
    for (const key in data) {
      if (Object.prototype.hasOwnProperty.call(data, key)) {
        const value = data[key]
        if (isHealthObject(value)) {
          if (hasSubSystem(value)) {
            addHealthObject(result, false, value, getModuleName(path, key))
            flattenHealthData(result, getModuleName(path, key), value.components || {})
          } else {
            addHealthObject(result, true, value, getModuleName(path, key))
          }
        }
      }
    }
  }

  const isHealthObject = (healthObject: HealthComponent): boolean => {
    return healthObject.status !== undefined
  }

  const hasSubSystem = (healthObject: HealthComponent): boolean => {
    if (!healthObject.components) return false
    return Object.values(healthObject.components).some(
      component => component.status !== undefined
    )
  }

  const getModuleName = (path: string | null, name: string): string => {
    return path ? `${path}.${name}` : name
  }

  const addHealthObject = (
    result: HealthData[],
    isLeaf: boolean,
    healthObject: HealthComponent,
    name: string
  ): HealthData => {
    const healthData: HealthData = {
      name,
      status: healthObject.status || "UNKNOWN",
      details: {},
      error: healthObject.error
    }

    const details: Record<string, unknown> = {}
    let hasDetails = false

    for (const key in healthObject) {
      if (
        Object.prototype.hasOwnProperty.call(healthObject, key) &&
        !["status", "error", "components"].includes(key)
      ) {
        details[key] = healthObject[key]
        hasDetails = true
      }
    }

    if (hasDetails) {
      healthData.details = details
    }

    if (isLeaf || hasDetails || healthData.error) {
      result.push(healthData)
    }

    return healthData
  }

  // Initial load
  useEffect(() => {
    refresh()
  }, [])

  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      <Card>
        <CardHeader className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <CardTitle>Health Checks</CardTitle>
          <Button variant="outline" className="gap-2" onClick={refresh}>
            <IconRefresh className="size-4" />
            Refresh
          </Button>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-[50px]">Show Details</TableHead>
                <TableHead className="w-[400px]">Service name</TableHead>
                <TableHead className="w-[100px]">Status</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {healthList.map((row) => (
                <TableRow key={row.name}>
                  <TableCell>
                    {(row.details || row.error) && (
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => showDetails(row)}
                        className="size-8 p-0"
                      >
                        <IconEye className="size-5 text-muted-foreground" />
                      </Button>
                    )}
                  </TableCell>
                  <TableCell className="font-medium">{row.name}</TableCell>
                  <TableCell>
                    <Badge
                      className={
                        row.status === "DOWN" ? "bg-red-600/10 dark:bg-red-600/20 hover:bg-red-600/10 text-red-500 shadow-none" :
                          row.status === "UNKNOWN" ? "bg-yellow-600/10 dark:bg-yellow-600/20 hover:bg-yellow-600/10 text-yellow-500 shadow-none" :
                            "bg-green-600/10 dark:bg-green-600/20 hover:bg-green-600/10 text-green-500 shadow-none"
                      }
                    >
                      {row.status}
                    </Badge>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {/* Drawer for details */}
      <Drawer open={showDrawer} onOpenChange={setShowDrawer}>
        <div className="mx-auto w-full max-w-sm">
          <DrawerContent>
            <DrawerHeader>
              <DrawerTitle>Health Check Details</DrawerTitle>
            </DrawerHeader>
            <div className="p-4 overflow-auto">
              <pre className="rounded-md bg-muted p-4 overflow-auto text-xs">
                <code>{JSON.stringify(modalData, null, 2)}</code>
              </pre>
            </div>
          </DrawerContent>
        </div>
      </Drawer>
    </LayoutBody>
  )
}