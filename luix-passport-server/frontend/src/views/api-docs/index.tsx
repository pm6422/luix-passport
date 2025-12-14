import { LayoutBody } from "@/layouts/layout-definitions"
import { Card } from "@/components/ui/card"
import { RoleDeveloper } from "@/permission/role-control"

export default function ApiDocs() {
  return (
    <RoleDeveloper>
      <LayoutBody className="flex flex-col" fixedHeight>
        <Card className="py-0">
          <iframe
            src="/swagger-ui/index.html"
            className="rounded-lg"
            width="100%"
            height="900"
          ></iframe>
        </Card>
      </LayoutBody>
    </RoleDeveloper>
  )
}
