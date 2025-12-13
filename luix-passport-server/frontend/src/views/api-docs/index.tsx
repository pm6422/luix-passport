import { LayoutBody } from "@/layouts/layout-definitions"
import { Card } from "@/components/ui/card"
import { RoleAny } from "@/components/custom/role/role-any"

export default function ApiDocs() {
  return (
    <RoleAny roleIds={["ROLE_DEVELOPER"]}>
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
    </RoleAny>
  )
}
