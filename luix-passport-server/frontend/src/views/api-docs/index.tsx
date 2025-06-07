import { LayoutBody } from "@/layouts/layout-definitions"
import { Card } from "@/components/ui/card"

export default function ApiDocs() {
  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      <Card>
        <iframe
          src="/swagger-ui/index.html"
          className="rounded-lg"
          width="100%"
          height="900"
        ></iframe>
      </Card>
    </LayoutBody>
  )
}
