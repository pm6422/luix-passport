import { Card } from "@/components/ui/card"

export default function ApiDocs() {
  return (
    <Card>
      <iframe src="/swagger-ui/index.html" width="100%" height="900"></iframe>
    </Card>
  )
}
