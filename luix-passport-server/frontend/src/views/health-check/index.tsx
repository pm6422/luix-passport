import { LayoutBody } from "@/layouts/layout-definitions"
import { useState } from "react"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { RefreshCw, Eye } from "lucide-react"
import { ManagementService } from "@/services/management-service"

interface HealthData {
  name: string
  status: 'UP' | 'DOWN' | 'UNKNOWN'
  details: Record<string, unknown>
  error?: string
}

interface HealthComponent {
  status?: 'UP' | 'DOWN' | 'UNKNOWN'
  components?: Record<string, HealthComponent>
  details?: Record<string, unknown>
  error?: string
  [key: string]: unknown
}

export default function HealthChecksPage() {
  const [healthList, setHealthList] = useState<HealthData[]>([])
  const [modalData, setModalData] = useState<Record<string, unknown>>({})
  const [showModal, setShowModal] = useState(false)

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
            name: 'Health API',
            status: 'DOWN',
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
    setShowModal(true)
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
      status: healthObject.status || 'UNKNOWN',
      details: {},
      error: healthObject.error
    }

    const details: Record<string, unknown> = {}
    let hasDetails = false

    for (const key in healthObject) {
      if (
        Object.prototype.hasOwnProperty.call(healthObject, key) &&
        !['status', 'error', 'components'].includes(key)
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
  useState(() => {
    refresh()
  }, [])

  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      <Card className="py-0">
        <CardHeader className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <CardTitle>Health Checks</CardTitle>
          <Button variant="outline" className="gap-2" onClick={refresh}>
            <RefreshCw className="h-4 w-4" />
            Refresh
          </Button>
        </CardHeader>

        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-[200px]">Service name</TableHead>
                <TableHead className="w-[100px]">Status</TableHead>
                <TableHead className="text-right">Details</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {healthList.map((row) => (
                <TableRow key={row.name}>
                  <TableCell className="font-medium">{row.name}</TableCell>
                  <TableCell>
                    <Badge
                      variant="outline"
                      className={
                        row.status === 'DOWN' ? 'bg-red-100 text-red-800 border-red-200' :
                          row.status === 'UNKNOWN' ? 'bg-yellow-100 text-yellow-800 border-yellow-200' :
                            'bg-green-100 text-green-800 border-green-200'
                      }
                    >
                      {row.status}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-right">
                    {(row.details || row.error) && (
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => showDetails(row)}
                      >
                        <Eye className="h-4 w-4" />
                      </Button>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {/* Modal for details */}
      {showModal && (
        <Card className="mt-4">
          <CardHeader>
            <CardTitle>Health Check Details</CardTitle>
          </CardHeader>
          <CardContent>
            <pre className="rounded-md bg-muted p-4 overflow-auto max-h-[400px]">
              <code>{JSON.stringify(modalData, null, 2)}</code>
            </pre>
            <div className="mt-4 flex justify-end">
              <Button onClick={() => setShowModal(false)}>
                Close
              </Button>
            </div>
          </CardContent>
        </Card>
      )}
    </LayoutBody>
  )
}